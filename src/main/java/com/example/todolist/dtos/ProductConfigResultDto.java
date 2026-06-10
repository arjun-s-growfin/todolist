package com.example.todolist.dtos;

public record ProductConfigResultDto(
        long accountId,
        long productId,
        String message
) {}
