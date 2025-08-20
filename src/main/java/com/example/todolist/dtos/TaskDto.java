package com.example.todolist.dtos;

import com.example.todolist.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskDto (
     String name,
     String description,
     TaskStatus taskStatus,
     LocalDateTime startDate,
     LocalDateTime dueDate
){}