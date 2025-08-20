package com.example.todolist.service;

import com.example.todolist.dtos.TaskDto;
import com.example.todolist.exceptions.BadRequestException;
import com.example.todolist.models.Task;
import com.example.todolist.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<TaskDto> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(Task::toDto)
                .toList();
    }

    public TaskDto getTask(Long taskId){
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if(taskOptional.isEmpty())
            throw new BadRequestException("No task found with id : " + taskId);
        return taskOptional.get().toDto();
    }

    public void addTask(TaskDto taskDto){

        Task task = Task.builder()
                .name(taskDto.name())
                .description(taskDto.description())
                .startDate(taskDto.startDate())
                .dueDate(taskDto.dueDate())
                .taskStatus(taskDto.taskStatus())
                .build();
        taskRepository.save(task);
    }

    public void updateTask(Long taskId, TaskDto taskDto){

        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if(taskOptional.isEmpty()) {
            throw new BadRequestException("task not found to update with taskId : " + taskId);
        }

        Task task = taskOptional.get();
        if(StringUtils.hasLength(taskDto.name()))
            task.setName(taskDto.name());
        if(StringUtils.hasLength(taskDto.description()))
            task.setDescription(taskDto.description());
        taskRepository.save(task);
    }

    public void deleteTask(Long id){
        if(!taskRepository.existsById(id)) {
            throw new BadRequestException("Task not found to delete with id : " + id);
        }
        taskRepository.deleteById(id);
    }

    public void deleteAllTasks(){
        taskRepository.deleteAll();
    }
}