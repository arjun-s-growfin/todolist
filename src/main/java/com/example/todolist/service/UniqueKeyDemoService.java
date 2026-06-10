package com.example.todolist.service;

import com.example.todolist.models.UniqueKeyItem;
import com.example.todolist.repositories.UniqueKeyItemRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UniqueKeyDemoService {

    private final UniqueKeyItemRepository repo;
    private final EntityManager em;

    /**
     * Seeds some rows so we have data to delete+re-insert.
     */
    @Transactional
    public void seed(List<String> codes) {
        codes.forEach(code -> repo.save(UniqueKeyItem.builder().code(code).build()));
        log.info("Seeded codes: {}", codes);
    }

    /**
     * BROKEN: Hibernate queues inserts BEFORE deletes in its action queue,
     * so the unique constraint fires even though deletes logically come first.
     */
    @Transactional
    public void deleteAndReinsertBroken(List<String> codes) {
        log.info("=== BROKEN: delete then insert, no flush in between ===");

        // Step 1 — delete existing rows
        repo.deleteByCodeIn(codes);
        log.info("Called deleteByCodeIn — rows are in Hibernate action queue, NOT yet sent to DB");

        // Step 2 — insert the same codes again
        // Hibernate will flush inserts before deletes → unique constraint violation
        codes.forEach(code -> repo.save(UniqueKeyItem.builder().code(code).build()));
        log.info("Called save for each code — Hibernate will try INSERT before DELETE");

        // flush happens at commit → BOOM: Duplicate entry
    }

    /**
     * FIXED: explicit flush after delete forces the DELETE to hit DB before INSERT.
     */
    @Transactional
    public void deleteAndReinsertFixed(List<String> codes) {
        log.info("=== FIXED: flush after delete, then insert ===");

        repo.deleteByCodeIn(codes);
        em.flush(); // forces DELETE SQL to execute now
        log.info("Flushed — DELETEs are in DB, safe to INSERT");

        codes.forEach(code -> repo.save(UniqueKeyItem.builder().code(code).build()));
        log.info("Inserts queued — no constraint violation, commits cleanly");
    }
}
