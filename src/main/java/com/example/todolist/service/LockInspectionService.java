package com.example.todolist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LockInspectionService {

    private final JdbcTemplate jdbcTemplate;

    public void logDataLocks(String label) {
        // Current InnoDB transaction ID on this connection — empty if no active trx
        List<Map<String, Object>> trxRows = jdbcTemplate.queryForList(
                "SELECT TRX_ID FROM information_schema.INNODB_TRX WHERE TRX_MYSQL_THREAD_ID = CONNECTION_ID()");
        String currentTrxId = trxRows.isEmpty() ? "none" : String.valueOf(trxRows.get(0).get("TRX_ID"));

        List<Map<String, Object>> locks = jdbcTemplate.queryForList(
                "SELECT ENGINE_TRANSACTION_ID, OBJECT_NAME, INDEX_NAME, LOCK_TYPE, LOCK_MODE, LOCK_STATUS, LOCK_DATA " +
                        "FROM performance_schema.data_locks WHERE OBJECT_NAME = 'ttable'");

        if (locks.isEmpty()) {
            log.debug("[locks][{}] current_trx={} — NO locks on ttable", label, currentTrxId);
        } else {
            log.debug("[locks][{}] current_trx={} — {} lock(s) on ttable:", label, currentTrxId, locks.size());
            locks.forEach(row -> log.debug("[locks][{}]   {}", label, row));
        }
    }
}
