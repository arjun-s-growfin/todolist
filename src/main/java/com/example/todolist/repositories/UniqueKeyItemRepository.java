package com.example.todolist.repositories;

import com.example.todolist.models.UniqueKeyItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UniqueKeyItemRepository extends JpaRepository<UniqueKeyItem, Long> {
    List<UniqueKeyItem> findByCodeIn(List<String> codes);
    void deleteByCodeIn(List<String> codes);
}
