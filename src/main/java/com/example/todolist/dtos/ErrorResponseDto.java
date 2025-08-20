package com.example.todolist.dtos;

import org.springframework.http.HttpStatus;

public record ErrorResponseDto (
    String message,
    HttpStatus httpStatus
){}
