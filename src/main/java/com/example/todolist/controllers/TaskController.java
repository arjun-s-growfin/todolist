package com.example.todolist.controllers;

import com.example.todolist.dtos.ApiResponseDto;
import com.example.todolist.dtos.TaskDto;
import com.example.todolist.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(
            @PathVariable("id") Long taskId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(taskService.getTask(taskId));
    }

    @PostMapping(path = "/add")
    public ResponseEntity<ApiResponseDto> addTask(
            @RequestBody TaskDto taskDto) throws JsonProcessingException {
        taskService.addTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto("Task added"));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ApiResponseDto> updateTask(
            @PathVariable("id") Long taskId,
            @RequestBody TaskDto taskDto){
        taskService.updateTask(taskId, taskDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseDto("Task updated"));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ApiResponseDto> deleteTask(
            @PathVariable("id") Long id){
        taskService.deleteTask(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponseDto("Task Deleted"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponseDto> deleteAllTasks(){
        taskService.deleteAllTasks();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDto("All tasks deleted"));
    }
}
