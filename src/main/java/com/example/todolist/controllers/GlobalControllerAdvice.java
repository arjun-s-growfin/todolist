package com.example.todolist.controllers;

import com.example.todolist.dtos.ErrorResponseDto;
import com.example.todolist.exceptions.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(
            final BadRequestException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST)
                );
    }
}
