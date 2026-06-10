package com.example.todolist.repositories;

import com.example.todolist.models.TTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TTableRepository extends JpaRepository<TTable, Long> {

    @Modifying
    @Query(value = "INSERT INTO ttable (foo) VALUES (:v1), (:v2) ON DUPLICATE KEY UPDATE foo = VALUES(foo)",
            nativeQuery = true)
    void batchUpsert(@Param("v1") int v1, @Param("v2") int v2);

    @Modifying
    @Query(value = "INSERT INTO ttable (foo) VALUES (:v) ON DUPLICATE KEY UPDATE foo = VALUES(foo)",
            nativeQuery = true)
    void singleUpsert(@Param("v") int v);
}
