package com.example.todolist.controllers;

import com.example.todolist.service.TaskService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public String testFlux() {
        return taskService.testFlux();
    }
}
