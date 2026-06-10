package com.example.todolist.service;

import com.example.todolist.repositories.TTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TTableService {

    private final TTableRepository tTableRepository;
    private final TTableInnerService tTableInnerService;
    private final LockInspectionService lockInspectionService;

    // Path 1: both values in a single INSERT ... ON DUPLICATE KEY UPDATE
    @Transactional
    public void runPath1() {
        log.debug("[path1] transaction begin — batch upsert (699422, 699421)");
        tTableRepository.batchUpsert(699422, 699421);
        log.debug("[path1] transaction committing");
    }

    // Path 2: outer transaction; each value inserted in its own REQUIRES_NEW inner transaction
    @Transactional
    public void runPath2() {
        log.debug("[path2] outer transaction begin");
        for (int v : List.of(699439, 699439)) {
            log.debug("[path2] delegating value={} to REQUIRES_NEW inner transaction", v);
            tTableInnerService.insertSingleRow(v);
            // Inner transaction has committed — its locks should be released
            lockInspectionService.logDataLocks("after-inner-tx-commit value=" + v);
            log.debug("[path2] inner transaction for value={} committed, outer resumed", v);
        }
        log.debug("[path2] outer transaction committing");
    }
}
