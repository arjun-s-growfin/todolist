package com.example.todolist.models;

import com.example.todolist.dtos.TaskDto;
import com.example.todolist.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity(name = Task.TABLE_NAME)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
public class Task {

    public static final String TABLE_NAME = "tasks";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;

    private LocalDateTime startDate;
    private LocalDateTime dueDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (taskStatus == null) {
            taskStatus = TaskStatus.TODO;
        }
        if (startDate == null) {
            startDate = LocalDateTime.now();
        }
    }

    public TaskDto toDto(){
        return new TaskDto(
                name, description, taskStatus, startDate, dueDate);
    }
}