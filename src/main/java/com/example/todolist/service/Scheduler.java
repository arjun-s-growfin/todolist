package com.example.todolist.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Scheduler {

    private final DueTasksProcessorService dueTasksProcessorService;

    @Scheduled(fixedRate = 20000)
    public void execute(){
        dueTasksProcessorService.processDueTasks();
    }
}
