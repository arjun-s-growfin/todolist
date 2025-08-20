package com.example.todolist.service;

import com.example.todolist.dtos.TaskDto;
import com.example.todolist.enums.TaskStatus;
import com.example.todolist.models.Task;
import com.example.todolist.repositories.TaskRepository;
import com.example.todolist.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DueTasksProcessorService {

    private final TaskRepository taskRepository;
    private final RabbitMQProducer rabbitMQProducer;
    private final Utils utils;

    public void processDueTasks(){

        List<Task> recentTasks = taskRepository.findByUpdatedAtAfter(LocalDateTime.now().minusSeconds(20));

        for(Task task: recentTasks){
            if(!task.getTaskStatus().equals(TaskStatus.COMPLETED) &&
            task.getDueDate().isBefore(LocalDateTime.now())){
                System.out.println("Pending task, exceeded due date " + task);
                rabbitMQProducer.sendMessage(utils.convertObjectToString(task));
            }
        }
    }
}
