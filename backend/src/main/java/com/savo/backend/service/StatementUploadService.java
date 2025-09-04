package com.savo.backend.service;

import com.savo.backend.dto.statementupload.StatementUploadRequestDTO;
import com.savo.backend.dto.statementupload.StatementUploadResponseDTO;
import com.savo.backend.enums.UploadStatus;
import com.savo.backend.exception.ValidationException;
import com.savo.backend.model.BankAccount;
import com.savo.backend.model.StatementUpload;
import com.savo.backend.model.User;
import com.savo.backend.repository.BankAccountRepository;
import com.savo.backend.repository.StatementUploadRepository;
import com.savo.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@Transactional
public class StatementUploadService {

    private static final Logger logger = LoggerFactory.getLogger(StatementUploadService.class);

    private final StatementUploadRepository statementUploadRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final BankAccountRepository bankAccountRepository;

    public StatementUploadService(StatementUploadRepository statementUploadRepository, UserRepository userRepository, FileStorageService fileStorageService, BankAccountRepository bankAccountRepository) {
        this.statementUploadRepository = statementUploadRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.bankAccountRepository = bankAccountRepository;
    }

    public StatementUploadResponseDTO processStatementUpload(MultipartFile file, String userId, String bankAccountId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));

        BankAccount bankAccount = bankAccountRepository.findByUserIdAndId(userId, bankAccountId)
                .orElseThrow(() -> new ValidationException("Bank account not found"));

        //validateNoDuplicateUpload(file, bankAccount);

        String s3Key = fileStorageService.uploadFile(file, userId, "statements");

        StatementUpload upload = new StatementUpload();
        upload.setId(userId);
        upload.setUser(user);
        upload.setBankAccount(bankAccount);
        upload.setFileName(file.getOriginalFilename());
        upload.setFileSize(file.getSize());
        upload.setS3Key(s3Key);
        upload.setUploadStatus(UploadStatus.PROCESSING);
        upload.setProcessingStartedAt(LocalDateTime.now());
        upload.setCreatedAt(LocalDateTime.now());

        StatementUpload savedUpload = statementUploadRepository.save(upload);
        return StatementUploadResponseDTO.from(savedUpload);
    }
}
