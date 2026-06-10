package com.example.todolist.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Benchmarks different DELETE strategies on bench_test.integ_job.
 *
 * Endpoints:
 *   POST   /integ-job/seed              — (re)create table and insert 50k rows for account_id=1
 *   GET    /integ-job/count             — current row count
 *   DELETE /integ-job/single            — one big DELETE WHERE account_id=1
 *   DELETE /integ-job/batched           — loop: DELETE … LIMIT batchSize (default 1000)
 *   DELETE /integ-job/batched-by-id     — fetch IDs in pages, delete IN (...) per page
 */
@RestController
@RequestMapping("integ-job")
@RequiredArgsConstructor
@Slf4j
public class IntegJobDeleteController {

    private static final String TABLE = "bench_test.integ_job";

    private final JdbcTemplate jdbcTemplate;

    // -------------------------------------------------------------------------
    // Seed
    // -------------------------------------------------------------------------

    @PostMapping("/seed")
    public Map<String, Object> seed() {
        jdbcTemplate.execute("CREATE DATABASE IF NOT EXISTS bench_test");
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + TABLE);
        jdbcTemplate.execute("""
                CREATE TABLE bench_test.integ_job (
                  id             bigint NOT NULL AUTO_INCREMENT,
                  account_id     int    NOT NULL,
                  type           varchar(128) NOT NULL,
                  priority       int    NOT NULL,
                  status         varchar(20)  NOT NULL,
                  parallelism    int    NOT NULL DEFAULT 10,
                  payload        json   DEFAULT NULL,
                  attempt        int    NOT NULL DEFAULT 0,
                  created_at     datetime(6)  NOT NULL,
                  updated_at     datetime(6)  NOT NULL,
                  processing_type varchar(20) NOT NULL DEFAULT 'ACCUMULATOR',
                  PRIMARY KEY (id),
                  KEY idx_account_status_priority (account_id, status, priority DESC)
                ) ENGINE=InnoDB
                """);

        jdbcTemplate.execute("""
                INSERT INTO bench_test.integ_job
                    (account_id, type, priority, status, created_at, updated_at)
                SELECT
                  1,
                  'SYNC_INVOICES',
                  FLOOR(RAND()*10),
                  ELT(FLOOR(1 + RAND()*3), 'COMPLETED', 'FAILED', 'QUEUED'),
                  NOW(6),
                  NOW(6)
                FROM
                  (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
                   UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
                  (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
                   UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
                  (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
                   UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c,
                  (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
                   UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) d,
                  (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) e
                LIMIT 50000
                """);

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + TABLE, Integer.class);
        return Map.of("seeded", count);
    }

    // -------------------------------------------------------------------------
    // Count
    // -------------------------------------------------------------------------

    @GetMapping("/count")
    public Map<String, Object> count() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + TABLE, Integer.class);
        return Map.of("count", count);
    }

    // -------------------------------------------------------------------------
    // Strategy 1: single DELETE (no LIMIT)
    // -------------------------------------------------------------------------

    @DeleteMapping("/single")
    public Map<String, Object> deleteSingle(
            @RequestParam(defaultValue = "1") int accountId) {

        long start = System.currentTimeMillis();
        int deleted = jdbcTemplate.update(
                "DELETE FROM " + TABLE + " WHERE account_id = ?", accountId);
        long ms = System.currentTimeMillis() - start;

        log.info("[single] deleted={} ms={}", deleted, ms);
        return result("single", deleted, ms, 1);
    }

    // -------------------------------------------------------------------------
    // Strategy 2: batched DELETE … ORDER BY id LIMIT ?
    // -------------------------------------------------------------------------

    @DeleteMapping("/batched")
    public Map<String, Object> deleteBatched(
            @RequestParam(defaultValue = "1") int accountId,
            @RequestParam(defaultValue = "1000") int batchSize) {

        long start = System.currentTimeMillis();
        int totalDeleted = 0;
        int batches = 0;

        int rows;
        do {
            rows = jdbcTemplate.update(
                    "DELETE FROM " + TABLE + " WHERE account_id = ? ORDER BY id LIMIT ?",
                    accountId, batchSize);
            totalDeleted += rows;
            batches++;
            log.debug("[batched] batch={} rows={}", batches, rows);
        } while (rows > 0);

        long ms = System.currentTimeMillis() - start;
        log.info("[batched] batchSize={} batches={} deleted={} ms={}", batchSize, batches, totalDeleted, ms);
        return result("batched (batchSize=" + batchSize + ")", totalDeleted, ms, batches);
    }

    // -------------------------------------------------------------------------
    // Strategy 3: fetch all IDs upfront, partition, delete IN (…) per partition
    // -------------------------------------------------------------------------

    @DeleteMapping("/batched-by-id")
    public Map<String, Object> deleteBatchedById(
            @RequestParam(defaultValue = "1") int accountId,
            @RequestParam(defaultValue = "1000") int batchSize) {

        long start = System.currentTimeMillis();

        List<Long> allIds = jdbcTemplate.queryForList(
                "SELECT id FROM " + TABLE + " WHERE account_id = ?",
                Long.class, accountId);

        log.info("[batched-by-id] fetched {} ids", allIds.size());

        int totalDeleted = 0;
        int batches = 0;

        for (int i = 0; i < allIds.size(); i += batchSize) {
            List<Long> partition = allIds.subList(i, Math.min(i + batchSize, allIds.size()));
            String placeholders = "?,".repeat(partition.size()).replaceAll(",$", "");
            int deleted = jdbcTemplate.update(
                    "DELETE FROM " + TABLE + " WHERE id IN (" + placeholders + ")",
                    partition.toArray());
            totalDeleted += deleted;
            batches++;
            log.debug("[batched-by-id] batch={} size={} deleted={}", batches, partition.size(), deleted);
        }

        long ms = System.currentTimeMillis() - start;
        log.info("[batched-by-id] batchSize={} batches={} deleted={} ms={}", batchSize, batches, totalDeleted, ms);
        return result("batched-by-id (batchSize=" + batchSize + ")", totalDeleted, ms, batches);
    }

    // -------------------------------------------------------------------------

    private Map<String, Object> result(String strategy, int deleted, long ms, int batches) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("strategy", strategy);
        m.put("rowsDeleted", deleted);
        m.put("batches", batches);
        m.put("totalMs", ms);
        m.put("msPerBatch", batches > 0 ? ms / batches : 0);
        return m;
    }
}
