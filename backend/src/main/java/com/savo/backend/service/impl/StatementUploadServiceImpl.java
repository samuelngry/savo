package com.savo.backend.service.impl;

import com.savo.backend.repository.StatementUploadRepository;
import com.savo.backend.service.StatementUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StatementUploadServiceImpl implements StatementUploadService {

    private static final Logger logger = LoggerFactory.getLogger(StatementUploadServiceImpl.class);

    private final StatementUploadRepository statementUploadRepository;

    @Value("${file.upload.max-size:10485760}") // 10MB default
    private long maxFileSize;

    @Value("${file.upload.allowed-types:application/pdf}")
    private String allowedFileTypes;

    public StatementUploadServiceImpl(StatementUploadRepository statementUploadRepository) {
        this.statementUploadRepository = statementUploadRepository;
    }
}
