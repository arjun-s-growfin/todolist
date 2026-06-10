package com.example.todolist.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class RawDataEntryDto {
    
    @NonNull
    private final Long id;

    @NonNull
    private final String rawData;
}
