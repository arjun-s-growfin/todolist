package com.example.todolist.models2;

import com.google.gson.annotations.Expose;

import lombok.Getter;

public abstract class AbstractIdModel implements IdExtractModel {

    @Getter
    @Expose(serialize = false, deserialize = true)
    private boolean deleted;
}