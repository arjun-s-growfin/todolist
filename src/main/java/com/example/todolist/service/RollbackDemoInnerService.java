package com.example.todolist.service;

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
public class RollbackDemoInnerService {

    private final TaskRepository taskRepository;

    /**
     * Participates in the caller's transaction (REQUIRED by default).
     * Throws RuntimeException — Spring's proxy marks the shared transaction as rollback-only
     * before re-throwing, regardless of whether the caller catches it.
     */
    @Transactional
    public void riskyOperation(String flow) {
        log.debug("[{}] INNER  >> isRollbackOnly BEFORE save   = {}", flow,
                TransactionAspectSupport.currentTransactionStatus().isRollbackOnly());

        taskRepository.save(Task.builder().name("inner-task-" + flow).build());

        log.debug("[{}] INNER  >> isRollbackOnly AFTER save    = {}", flow,
                TransactionAspectSupport.currentTransactionStatus().isRollbackOnly());

        log.debug("[{}] INNER  >> about to throw RuntimeException", flow);

        // This unchecked exception causes Spring's @Transactional proxy on THIS bean
        // to mark the current (shared) transaction as rollback-only before re-throwing.
        throw new RuntimeException("Deliberate failure inside inner service [" + flow + "]");
    }
}
