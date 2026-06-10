package com.example.todolist.controllers;

import com.example.todolist.service.TTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("ttable")
@RequiredArgsConstructor
@Slf4j
public class TTableController {

    private static final int ITERATIONS = 100;

    private final TTableService tTableService;
    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/run")
    public String run() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS ttable");
        jdbcTemplate.execute("""
                CREATE TABLE ttable (
                    id BIGINT NOT NULL AUTO_INCREMENT,
                    foo INT NOT NULL,
                    PRIMARY KEY (id),
                    UNIQUE KEY index_unique (foo)
                )
                """);

        AtomicInteger path1Errors = new AtomicInteger();
        AtomicInteger path2Errors = new AtomicInteger();

        // Unbounded pool so all tasks start immediately, matching bash's `&` accumulation.
        // Actual parallelism is capped by the DB connection pool size.
        ExecutorService executor = Executors.newCachedThreadPool();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < ITERATIONS; i++) {
            final int iter = i;

            // All path1 submissions are in-flight at once — multiple concurrent
            // instances contend for the same rows (699422, 699421) → S→X upgrade deadlock
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    tTableService.runPath1();
                } catch (Exception e) {
                    path1Errors.incrementAndGet();
                    log.warn("path1[{}] error: {}", iter, e.getMessage());
                }
            }, executor));

            // Same for path2 — concurrent instances contend for (699439, 699439)
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    tTableService.runPath2();
                } catch (Exception e) {
                    path2Errors.incrementAndGet();
                    log.warn("path2[{}] error: {}", iter, e.getMessage());
                }
            }, executor));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        return String.format("done — path1 errors: %d, path2 errors: %d",
                path1Errors.get(), path2Errors.get());
    }
}
