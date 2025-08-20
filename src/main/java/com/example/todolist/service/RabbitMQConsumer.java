package com.example.todolist.service;

import com.example.todolist.configs.RabbitMQConfig;
import com.example.todolist.models.Task;
import com.example.todolist.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final Utils utils;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void listen(String message){

        Task task = utils.converStringToObject(message, Task.class);
        System.out.println("Due task found : " + task);
    }
}

