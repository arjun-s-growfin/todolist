package com.example.todolist.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Utils {

    private final ObjectMapper objectMapper;

    public <T> String convertObjectToString(T object){
        try{
            return objectMapper.writeValueAsString(object);
        }
        catch (JsonProcessingException ex){
            throw new RuntimeException("Unable to write object " + object + " as string.");
        }
    }

    public <T> T converStringToObject(String string, Class<T> clazz){
        try{
            return objectMapper.readValue(string, clazz);
        }
        catch (JsonProcessingException ex){
            throw new RuntimeException("Unable to get object from string : " + string);
        }
    }

}
