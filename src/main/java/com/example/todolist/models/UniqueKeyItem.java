package com.example.todolist.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "unique_key_items", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniqueKeyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;
}
