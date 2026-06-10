package com.example.todolist.dtos;

import java.util.List;

public record UpdateConfigResult(
        boolean success,
        List<String> errors,
        Object data
) {}
