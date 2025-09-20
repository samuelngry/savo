package com.savo.backend.service;

import com.savo.backend.dto.statementupload.*;
import com.savo.backend.enums.UploadStatus;
import com.savo.backend.exception.ValidationException;
import com.savo.backend.model.BankAccount;
import com.savo.backend.model.StatementUpload;
import com.savo.backend.model.User;
import com.savo.backend.repository.BankAccountRepository;
import com.savo.backend.repository.StatementUploadRepository;
import com.savo.backend.repository.TransactionRepository;
import com.savo.backend.repository.UserRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    private final BankAccountService bankAccountService;
    private final BankDetectionService bankDetectionService;

    public StatementUploadService(StatementUploadRepository statementUploadRepository, UserRepository userRepository, FileStorageService fileStorageService, BankAccountRepository bankAccountRepository, TransactionRepository transactionRepository, BankAccountService bankAccountService, BankDetectionService bankDetectionService) {
        this.statementUploadRepository = statementUploadRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.bankAccountService = bankAccountService;
        this.bankDetectionService = bankDetectionService;
    }

    public StatementUploadResponseDTO processStatementUpload(MultipartFile file, String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));

        BankAccount bankAccount = bankDetectionService.detectAndResolveBankAccount(file, userId);

        validateNoDuplicateUpload(file, bankAccount);

        String s3Key = fileStorageService.uploadFile(file, userId, "statements");

        StatementUpload upload = createStatementUpload(file, user, bankAccount, s3Key);
        StatementUpload savedUpload = statementUploadRepository.save(upload);

        // Start background processing
        processStatementAsync(savedUpload.getId());

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
                    List<TransactionSample> sampleTransactions = extractSampleTransactions(file, bankAccount.getBankName(), 5);
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

    private LocalDate[] extractStatementPeriod(MultipartFile file, String bankAccountName) throws IOException {
        String pdfText = extractPDFText(file, 2);

        Pattern periodPattern = null;
        DateTimeFormatter dateFormatter = null;

        switch (bankAccountName.toUpperCase()) {
            case "DBS":
                // "as at 31 Jul 2025"
                periodPattern = Pattern.compile("as at (\\d{1,2}\\s+\\w{3}\\s+\\d{4})", Pattern.CASE_INSENSITIVE);
                dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
                break;

            case "OCBC":
                // "01 Jan 2024 TO 31 Jan 2024"
                periodPattern = Pattern.compile("(\\d{1,2}\\s+\\w{3}\\s+\\d{4})\\s+TO\\s+(\\d{1,2}\\s+\\w{3}\\s+\\d{4})", Pattern.CASE_INSENSITIVE);
                dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
                break;

            case "UOB":
                // "Period: 01 Jan 2024 to 31 Jan 2024"
                periodPattern = Pattern.compile("Period[:\\s]+(\\d{1,2}\\s+\\w{3}\\s+\\d{4})\\s+to\\s+(\\d{1,2}\\s+\\w{3}\\s+\\d{4})", Pattern.CASE_INSENSITIVE);
                dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
                break;
        }

        if (periodPattern != null && dateFormatter != null) {
            Matcher matcher = periodPattern.matcher(pdfText);

            if (matcher.find()) {
                try {
                    if (bankAccountName.equalsIgnoreCase("DBS")) {
                        LocalDate endDate = LocalDate.parse(matcher.group(1).trim(), dateFormatter);
                        LocalDate startDate = endDate.withDayOfMonth(1);
                        return new LocalDate[]{startDate, endDate};
                    }
                    LocalDate startDate = LocalDate.parse(matcher.group(1).trim(), dateFormatter);
                    LocalDate endDate = LocalDate.parse(matcher.group(2).trim(), dateFormatter);
                    return new LocalDate[]{startDate, endDate};
                } catch (Exception e) {
                    logger.warn("Failed to parse dates for bank {}: {}", bankAccountName, e.getMessage());
                }
            }
        }

        return null;
    }

    public UploadStatusResponseDTO getUploadStatus(String uploadId, String userId) {
        StatementUpload upload = statementUploadRepository.findByIdAndUserId(uploadId, userId)
                .orElseThrow(() -> new ValidationException("Upload not found"));

        return UploadStatusResponseDTO.from(upload);
    }

    public Page<UploadHistoryResponseDTO> getUploadHistory(String userId, Pageable pageable,
                                                           String bankName, String status) {

        Specification<StatementUpload> spec = Specification.where(null);

        spec = spec.and((root, query, builder) ->
                builder.equal(root.get("user").get("id"), userId));

        // Filter by bank name if provided
        if (bankName != null && !bankName.trim().isEmpty()) {
            spec = spec.and((root, query, builder) ->
                    builder.equal(root.get("bankAccount").get("bankName"), bankName.toUpperCase()));
        }

        // Filter by status if provided
        if (status != null && !status.trim().isEmpty()) {
            try {
                UploadStatus uploadStatus = UploadStatus.valueOf(status.toUpperCase());
                spec = spec.and((root, query, builder) ->
                        builder.equal(root.get("uploadStatus"), uploadStatus));
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore filter
                logger.warn("Invalid status filter: {}", status);
            }
        }

        // Order by creation date descending
        Page<StatementUpload> uploads = statementUploadRepository.findAll(spec, pageable);

        return uploads.map(UploadHistoryResponseDTO::from);
    }

    public void deleteUpload(String uploadId, String userId) {
        StatementUpload upload = statementUploadRepository.findByIdAndUserId(uploadId, userId)
                .orElseThrow(() -> new ValidationException("Upload not found"));

        // Only allow deletion if not currently processing
        if (upload.getUploadStatus() == UploadStatus.PROCESSING) {
            throw new ValidationException("Cannot delete upload while it's being processed");
        }

        try {
            // Delete associated transactions first
            if (upload.getUploadStatus() == UploadStatus.COMPLETED) {
                transactionRepository.deleteByStatementUploadId(uploadId);
                logger.info("Deleted transactions for upload: {}", uploadId);
            }

            // Delete file from S3
            fileStorageService.deleteFile(upload.getS3Key());

            statementUploadRepository.delete(upload);

            logger.info("Successfully deleted upload: {}", uploadId);

        } catch (Exception e) {
            logger.error("Failed to delete upload: {}", uploadId, e);
            throw new RuntimeException("Failed to delete upload", e);
        }
    }

    public StatementUploadResponseDTO retryUpload(String uploadId, String userId) {
        StatementUpload upload = statementUploadRepository.findByIdAndUserId(uploadId, userId)
                .orElseThrow(() -> new ValidationException("Upload not found"));

        // Only allow retry for failed uploads
        if (upload.getUploadStatus() != UploadStatus.FAILED) {
            throw new ValidationException("Only failed uploads can be retried");
        }

        // Check if file still exists in S3
        if (!fileStorageService.fileExists(upload.getS3Key())) {
            throw new ValidationException("Original file no longer exists and cannot be retried");
        }

        // Reset upload status and timestamps
        upload.setUploadStatus(UploadStatus.PROCESSING);
        upload.setProcessingStartedAt(LocalDateTime.now());
        upload.setProcessingCompletedAt(null);
        upload.setErrorMessage(null);
        upload.setTotalTransactionsExtracted(null);

        StatementUpload savedUpload = statementUploadRepository.save(upload);

        // Start background processing
        processStatementAsync(savedUpload.getId());

        return StatementUploadResponseDTO.from(savedUpload);
    }

    private List<TransactionSample> extractSampleTransactions(MultipartFile file, String bankAccountName, int sampleSize) throws IOException {
        String pdfText = extractPDFText(file, -1); // Read full document
        List<TransactionSample> samples = new ArrayList<>();
        int count = 0;

        switch (bankAccountName.toUpperCase()) {
            // TODO: Write correct transaction pattern and find year of transactions
            case "DBS":
                Pattern transactionPattern = Pattern.compile(
                        "(\\d{2}/\\d{2}/\\d{4})\\s+(.+?)\\s+(\\d{1,3}(?:,\\d{3})*\\.\\d{2}|-)\\s+(\\d{1,3}(?:,\\d{3})*\\.\\d{2}|-)",
                        Pattern.CASE_INSENSITIVE);

                Matcher matcher = transactionPattern.matcher(pdfText);

                while (matcher.find() && count < sampleSize) {
                    try {
                        // "Date: 01/08/2025"
                        String fullDateStr = matcher.group(1);
                        LocalDate date = LocalDate.parse(fullDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                        String description = matcher.group(2);

                        BigDecimal debit = matcher.group(3).equals("-") ? null : new BigDecimal(matcher.group(3).replace(",",""));
                        BigDecimal credit = matcher.group(4).equals("-") ? null : new BigDecimal(matcher.group(4).replace(",", ""));

                        BigDecimal amount = (debit != null) ? debit.negate() : (credit != null) ? credit : BigDecimal.ZERO;

                        samples.add(new TransactionSample(date, description, amount));
                        count++;
                    } catch (Exception e) {
                        logger.warn("Failed to parse DBS transaction line: {}", matcher.group(0));
                    }
                }

                break;

            case "OCBC":
                int currentYear = LocalDate.now().getYear(); // fallback

                try {
                    Pattern periodPattern = Pattern.compile("(\\d{1,2}\\s+\\w{3}\\s+(\\d{4}))\\s+TO\\s+(\\d{1,2}\\s+\\w{3}\\s+(\\d{4}))", Pattern.CASE_INSENSITIVE);
                    Matcher periodMatcher = periodPattern.matcher(pdfText);
                    if (periodMatcher.find()) {
                        currentYear = Integer.parseInt(periodMatcher.group(2)); // Start year
                    }
                } catch (Exception e) {
                    logger.warn("Failed to extract statement year for OCBC, defaulting to current year");
                }

                Pattern ocbcPattern = Pattern.compile(
                        "(\\d{2}\\s+\\w{3})\\s+\\d{2}\\s+\\w{3}\\s+(.+?)\\s+\\S+\\s+(\\d{1,3}(?:,\\d{3})*\\.\\d{2}|-)\\s+(\\d{1,3}(?:,\\d{3})*\\.\\d{2}|-)",
                        Pattern.CASE_INSENSITIVE);

                Matcher ocbcMatcher = ocbcPattern.matcher(pdfText);

                while (ocbcMatcher.find() && count < sampleSize) {
                    try {
                        String dateStr = ocbcMatcher.group(1) + " " + currentYear;
                        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH));

                        String description = ocbcMatcher.group(2);

                        BigDecimal withdrawal = ocbcMatcher.group(3).equals("-") ? null : new BigDecimal(ocbcMatcher.group(3).replace(",", ""));
                        BigDecimal deposit = ocbcMatcher.group(4).equals("-") ? null : new BigDecimal(ocbcMatcher.group(4).replace(",", ""));

                        BigDecimal amount = (withdrawal != null) ? withdrawal.negate() : (deposit != null) ? deposit : BigDecimal.ZERO;

                        samples.add(new TransactionSample(date, description, amount));
                        count++;
                    } catch (Exception e) {
                        logger.warn("Failed to parse OCBC transaction line: {}", ocbcMatcher.group(0));
                    }
                }
                break;

            case "UOB":
                Pattern uobPattern = Pattern.compile(
                        "(\\d{2}/\\d{2}/\\d{4})\\s+" +
                                "(\\d{2}/\\d{2}/\\d{4})\\s+" +
                                "(\\d{2}/\\d{2}/\\d{4}\\s+d{2}:\\d{2}:\\d{2}\\s+[AP]M)\\s+" +
                                "(.+?)\\s+" +
                                "(\\d{1,3}(?:,\\d{3})*\\.\\d{2})\\s+" +
                                "(\\d{1,3}(?:,\\d{3})*\\.\\d{2})\\s+" +
                                "(\\d{1,3}(?:,\\d{3})*\\.\\d{2})"
                );
                Matcher uobMatcher = uobPattern.matcher(pdfText);

                while (uobMatcher.find() && count < sampleSize) {
                    try {
                        String dateStr = uobMatcher.group(1);
                        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH));

                        String description = uobMatcher.group(4);

                        BigDecimal deposit = uobMatcher.group(5).equals("-") ? null : new BigDecimal(uobMatcher.group(5).replace(",", ""));
                        BigDecimal withdrawal = uobMatcher.group(6).equals("-") ? null : new BigDecimal(uobMatcher.group(6).replace(",", ""));

                        BigDecimal amount = (withdrawal != null) ? withdrawal.negate() : (deposit != null) ? deposit : BigDecimal.ZERO;

                        samples.add(new TransactionSample(date, description, amount));
                        count++;
                    } catch (Exception e) {
                        logger.warn("Failed to parse UOB transaction line: {}", uobMatcher.group(0));
                    }
                }
                break;
        }

        return samples;
    }

    private boolean checkForDuplicateTransactions(List<TransactionSample> samples, String bankAccountId) {
        for (TransactionSample sample : samples) {
            boolean exists = transactionRepository.existsByBankAccountIdAndDateAndDescriptionAndAmount(
                    bankAccountId, sample.getDate(), sample.getDescription(), sample.getAmount());

            if (exists) {
                logger.info("Found duplicate transaction: date={}, description={}, amount={}", sample.getDate(), sample.getDescription(), sample.getAmount());
                return true;
            }
        }
        return false;
    }

    // Fallback validation
    private void validateBasicFileDuplicate(MultipartFile file, BankAccount bankAccount) {
        String fileName = file.getOriginalFilename();
        long fileSize = file.getSize();

        boolean basicDuplicate = statementUploadRepository
                .existsByBankAccountIdAndFileNameAndFileSize(bankAccount.getId(), fileName, fileSize);

        if (basicDuplicate) {
            throw new ValidationException("A file with the same name and size has already been uploaded");
        }
    }

    private StatementUpload createStatementUpload(MultipartFile file, User user, BankAccount bankAccount, String s3Key) {
        StatementUpload upload = new StatementUpload();
        upload.setUser(user);
        upload.setBankAccount(bankAccount);
        upload.setFileName(file.getOriginalFilename());
        upload.setFileSize(file.getSize());
        upload.setS3Key(s3Key);
        upload.setUploadStatus(UploadStatus.PROCESSING);
        upload.setProcessingStartedAt(LocalDateTime.now());
        upload.setCreatedAt(LocalDateTime.now());

        try {
            LocalDate[] period = extractStatementPeriod(file, bankAccount.getBankName());
            if (period != null && period.length == 2) {
                upload.setStatementPeriodStart(period[0]);
                upload.setStatementPeriodEnd(period[1]);
            }
        } catch (Exception e) {
            logger.warn("Could not extract statement period during upload creation", e);
        }

        return upload;
    }

    @Async
    public void processStatementAsync(String uploadId) {
        try {
            StatementUpload upload = statementUploadRepository.findById(uploadId)
                    .orElseThrow(() -> new ValidationException("Upload not found"));

            logger.info("Starting statement processing for upload: {}", uploadId);

            // TODO: Add actual PDF parsing and transaction extraction here
            // statementParserService.parseAndSaveTransactions(upload);

            upload.setUploadStatus(UploadStatus.COMPLETED);
            upload.setProcessingCompletedAt(LocalDateTime.now());
            statementUploadRepository.save(upload);

            logger.info("Finished statement processing for upload: {}", uploadId);
        } catch (Exception e) {
            logger.error("Statement processing failed for upload: {}", uploadId, e);
            updateUploadStatus(uploadId, UploadStatus.FAILED, e.getMessage());
        }
    }

    private void updateUploadStatus(String uploadId, UploadStatus status, String errorMessage) {
        statementUploadRepository.findById(uploadId).ifPresent(upload -> {
            upload.setUploadStatus(status);
            upload.setErrorMessage(errorMessage);
            upload.setProcessingCompletedAt(LocalDateTime.now());
            statementUploadRepository.save(upload);
        });
    }

    private String extractPDFText(MultipartFile file, int maxPages) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();

            if (maxPages > 0) {
                stripper.setEndPage(Math.min(maxPages, document.getNumberOfPages()));
            }

            return stripper.getText(document);
        }
    }
}