package com.example.todolist.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ttable", uniqueConstraints = @UniqueConstraint(name = "index_unique", columnNames = "foo"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer foo;
}
