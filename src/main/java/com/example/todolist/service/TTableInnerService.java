package com.example.todolist.service;

import com.example.todolist.repositories.TTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TTableInnerService {

    private final TTableRepository tTableRepository;
    private final LockInspectionService lockInspectionService;

    // Each call opens its own independent DB transaction (suspends the caller's)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertSingleRow(int value) {
        log.debug("[path2-inner] REQUIRES_NEW transaction begin — singleUpsert value={}", value);
        tTableRepository.singleUpsert(value);
        // Still inside the REQUIRES_NEW transaction — locks acquired by this insert are visible here
        lockInspectionService.logDataLocks("inside-inner-tx value=" + value);
        log.debug("[path2-inner] REQUIRES_NEW transaction committing — value={}", value);
    }
}
