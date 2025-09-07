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
import com.savo.backend.repository.TransactionRepository;
import com.savo.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class StatementUploadService {

    private static final Logger logger = LoggerFactory.getLogger(StatementUploadService.class);

    private final StatementUploadRepository statementUploadRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    public StatementUploadService(StatementUploadRepository statementUploadRepository, UserRepository userRepository, FileStorageService fileStorageService, BankAccountRepository bankAccountRepository, TransactionRepository transactionRepository) {
        this.statementUploadRepository = statementUploadRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
    }

    public StatementUploadResponseDTO processStatementUpload(MultipartFile file, String userId, String bankAccountId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));

        BankAccount bankAccount = bankAccountRepository.findByUserIdAndId(userId, bankAccountId)
                .orElseThrow(() -> new ValidationException("Bank account not found"));

        // TODO: Prevent user from uploading duplicate bank statements from same bank account
        validateNoDuplicateUpload(file, bankAccount);

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

    private void validateNoDuplicateUpload(MultipartFile file, BankAccount bankAccount) {
        try {
            LocalDate[] period = extractStatementPeriod(file, bankAccount.getBankName());

            if (period != null && period.length == 2) {
                LocalDate startDate = period[0];
                LocalDate endDate = period[1];

                long existingTransactionCount = transactionRepository
                        .countByBankAccountIdAndDateRange(bankAccount.getId(), startDate, endDate);

                if (existingTransactionCount > 0) {
                    List<TransactionSample> sampleTransactions = extractSampleTransactions(file, 5);
                    boolean hasDuplicateTransactions = checkForDuplicateTransactions(sampleTransactions, bankAccount.getId());

                    if (hasDuplicateTransactions) {
                        throw new ValidationException(String.format(
                                "Statement for period %s to %s contains transactions that already exist",
                                startDate, endDate));
                    }

                    logger.warn("Period overlap detected but transaction differ: bankAccount={}, period={}-{}",
                            bankAccount.getId(), startDate, endDate);
                }
            }

            validateBasicFileDuplicate(file, bankAccount);

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Failed to perform deep duplicate validation: {}", e.getMessage(), e);
            validateBasicFileDuplicate(file, bankAccount);
        }
    }

    // TODO: Check each bank patterns matches
    private LocalDate[] extractStatementPeriod(MultipartFile file, String bankAccountName) throws IOException {
        String pdfText = extractPDFText(file, 2);

        Pattern periodPattern = null;
        DateTimeFormatter dateFormatter = null;

        switch (bankAccountName.toUpperCase()) {
            case "DBS":
                // "Statement Period: 01 Jan 2024 to 31 Jan 2024"
                periodPattern = Pattern.compile("Statement Period[:\\s]+(\\d{1,2}\\s+\\w{3}\\s+\\d{4})\\s+to\\s+(\\d{1,2}\\s+\\w{3}\\s+\\d{4})", Pattern.CASE_INSENSITIVE);
                dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
                break;

            case "OCBC":
                // "From 01/01/2024 To 31/01/2024"
                periodPattern = Pattern.compile("From\\s+(\\d{2}/\\d{2}/\\d{4})\\s+To\\s+(\\d{2}/\\d{2}/\\d{4})", Pattern.CASE_INSENSITIVE);
                dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                break;

            case "UOB":
                // "Period: 01 Jan 2024 - 31 Jan 2024"
                periodPattern = Pattern.compile("Period[:\\s]+(\\d{1,2}\\s+\\w{3}\\s+\\d{4})\\s*[-–]\\s*(\\d{1,2}\\s+\\w{3}\\s+\\d{4})", Pattern.CASE_INSENSITIVE);
                dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
                break;
        }

        if (periodPattern != null && dateFormatter != null) {
            Matcher matcher = periodPattern.matcher(pdfText);
            if (matcher.find()) {
                try {
                    LocalDate startDate = LocalDate.parse(matcher.group(1).trim(), dateFormatter);
                    LocalDate endDate = LocalDate.parse(matcher.group(2).trim(), dateFormatter);
                    return new LocalDate[]{startDate, endDate};
                } catch (Exception e) {
                    logger.warn("Failed to parse dates: {} to {}", matcher.group(1), matcher.group(2));
                }
            }
        }

        return null;
    }
}
