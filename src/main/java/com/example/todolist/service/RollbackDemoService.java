package com.example.todolist.service;

import com.example.todolist.exceptions.BusinessException;
import com.example.todolist.models.Task;
import com.example.todolist.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class RollbackDemoService {

    private final TaskRepository taskRepository;
    private final RollbackDemoInnerService innerService;

    /**
     * FLOW 1 — the trap.
     *
     * The inner service throws RuntimeException, which causes Spring to mark the
     * shared transaction as rollback-only. The outer method catches it and wraps
     * it as a checked BusinessException. Spring sees only the checked exception at
     * commit time and tries to commit — but the transaction is already rollback-only,
     * so Spring throws UnexpectedRollbackException instead.
     */
    @Transactional
    public String flow1WrappedChecked() throws BusinessException {
        log.debug("[flow1] OUTER >> isRollbackOnly at START               = {}", rollbackOnly());

        taskRepository.save(Task.builder().name("outer-task-flow1").build());
        log.debug("[flow1] OUTER >> isRollbackOnly after outer save       = {}", rollbackOnly());

        try {
            innerService.riskyOperation("flow1");
        } catch (RuntimeException e) {
            // After catching here, the transaction is ALREADY marked rollback-only
            // because Spring's proxy on innerService marked it before re-throwing.
            log.debug("[flow1] OUTER >> caught RuntimeException from inner");
            log.debug("[flow1] OUTER >> isRollbackOnly after catching RuntimeException = {}", rollbackOnly());
            log.debug("[flow1] OUTER >> wrapping as checked BusinessException and re-throwing");

            // Throwing a checked exception — Spring's @Transactional will NOT roll back
            // on its own for checked exceptions, so it will try to commit.
            // But rollback-only = true  →  UnexpectedRollbackException
            throw new BusinessException("Wrapped checked: " + e.getMessage(), e);
        }

        log.debug("[flow1] OUTER >> isRollbackOnly before return (should not reach) = {}", rollbackOnly());
        return "flow1 completed (unreachable)";
    }

    /**
     * FLOW 2 — clean propagation.
     *
     * The RuntimeException from the inner service propagates directly up through
     * this method. Spring's @Transactional proxy on THIS method sees the unchecked
     * exception, rolls back cleanly, and re-throws. No rollback-only confusion.
     */
    @Transactional
    public String flow2DirectPropagation() {
        log.debug("[flow2] OUTER >> isRollbackOnly at START               = {}", rollbackOnly());

        taskRepository.save(Task.builder().name("outer-task-flow2").build());
        log.debug("[flow2] OUTER >> isRollbackOnly after outer save       = {}", rollbackOnly());

        // No try-catch — RuntimeException propagates directly to this method's
        // @Transactional proxy, which rolls back and re-throws cleanly.
        innerService.riskyOperation("flow2");

        log.debug("[flow2] OUTER >> isRollbackOnly before return (should not reach) = {}", rollbackOnly());
        return "flow2 completed (unreachable)";
    }

    private boolean rollbackOnly() {
        return TransactionAspectSupport.currentTransactionStatus().isRollbackOnly();
    }
}
