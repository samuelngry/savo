package com.savo.backend.service.impl;

import com.savo.backend.repository.StatementUploadRepository;
import com.savo.backend.service.StatementUploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StatementUploadServiceImpl implements StatementUploadService {

    private final StatementUploadRepository statementUploadRepository;

    public StatementUploadServiceImpl(StatementUploadRepository statementUploadRepository) {
        this.statementUploadRepository = statementUploadRepository;
    }
}
