package com.savo.backend.controller;

import com.savo.backend.service.StatementUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/statements")
@CrossOrigin(origins = "*")
public class StatementUploadController {

    private static final Logger logger = LoggerFactory.getLogger(StatementUploadController.class);

    private final StatementUploadService statementUploadService;

    public StatementUploadController(StatementUploadService statementUploadService) {
        this.statementUploadService = statementUploadService;
    }

    @PostMapping
}
