package com.example.todolist.controllers;

import com.example.todolist.exceptions.BusinessException;
import com.example.todolist.service.RollbackDemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/rollback-demo")
@RequiredArgsConstructor
public class RollbackDemoController {

    private final RollbackDemoService rollbackDemoService;

    /**
     * Flow 1: RuntimeException caught inside @Transactional, wrapped as checked exception.
     *
     * Expected result:
     *   - Spring throws UnexpectedRollbackException ("Transaction rolled back because it
     *     has been marked as rollback-only") because the inner service already marked it
     *     rollback-only, but the outer method tried to commit (checked exception = no rollback).
     *
     * curl http://localhost:8089/rollback-demo/flow1
     */
    @GetMapping("/flow1")
    public ResponseEntity<String> flow1() {
        try {
            rollbackDemoService.flow1WrappedChecked();
            return ResponseEntity.ok("SUCCESS (should never happen)");
        } catch (UnexpectedRollbackException e) {
            log.error("[flow1] CONTROLLER >> caught UnexpectedRollbackException: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body("EXPECTED BOOM — UnexpectedRollbackException:\n" + e.getMessage());
        } catch (BusinessException e) {
            // In practice this is NOT reached — Spring replaces BusinessException with
            // UnexpectedRollbackException at the proxy boundary.
            log.error("[flow1] CONTROLLER >> caught BusinessException (unexpected path): {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body("BusinessException (unexpected):\n" + e.getMessage());
        }
    }

    /**
     * Flow 2: RuntimeException propagates uncaught through @Transactional.
     *
     * Expected result:
     *   - Clean rollback, no UnexpectedRollbackException.
     *   - Controller receives the original RuntimeException.
     *
     * curl http://localhost:8089/rollback-demo/flow2
     */
    @GetMapping("/flow2")
    public ResponseEntity<String> flow2() {
        try {
            rollbackDemoService.flow2DirectPropagation();
            return ResponseEntity.ok("SUCCESS (should never happen)");
        } catch (RuntimeException e) {
            log.error("[flow2] CONTROLLER >> caught RuntimeException (clean rollback, no boom): {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body("Clean rollback — RuntimeException:\n" + e.getMessage());
        }
    }
}
