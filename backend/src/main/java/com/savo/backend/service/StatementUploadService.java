package com.savo.backend.service;

import com.savo.backend.dto.statementupload.StatementUploadRequestDTO;
import com.savo.backend.dto.statementupload.StatementUploadResponseDTO;
import com.savo.backend.dto.statementupload.TransactionSample;
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

    // TODO: Get a few transactions from PDF to compare
    private List<TransactionSample> extractSampleTransactions(MultipartFile file, String bankAccountName, int sampleSize) throws IOException {
        String pdfText = extractPDFText(file, -1); // Read full document
        List<TransactionSample> samples = new ArrayList<>();
        int count = 0;

        switch (bankAccountName.toUpperCase()) {
            // "01 Jul McDonald's 15.50"
            // TODO: Write correct transaction pattern and find year of transactions
            case "DBS":
                int year = LocalDate.now().getYear(); // Fallback
                Pattern yearPattern = Pattern.compile("as at (\\d{1,2}\\s+\\w{3}\\s+\\d{4})", Pattern.CASE_INSENSITIVE);
                Matcher yearMatcher = yearPattern.matcher(pdfText);
                if (yearMatcher.find()) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
                        LocalDate parsedDate = LocalDate.parse(yearMatcher.group(1).trim(), formatter);
                        year = parsedDate.getYear();
                    } catch (Exception e) {
                        logger.warn("Failed to parse year: {}", yearMatcher.group(1));
                    }
                }

                Pattern transactionPattern = Pattern.compile(
                        "(\\d{2}\\s+\\w{3})\\s+(.+?)\\s+(\\d{1,3}(?:,\\d{3})*\\.\\d{2}|-)\\s+(\\d{1,3}(?:,\\d{3})*\\.\\d{2}|-)",
                        Pattern.CASE_INSENSITIVE);

                Matcher matcher = transactionPattern.matcher(pdfText);

                while (matcher.find() && count < sampleSize) {
                    try {
                        // Format 01 Jul 2025"
                        String fullDateStr = matcher.group(1) + " " + year;
                        LocalDate date = LocalDate.parse(fullDateStr, DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH));
                        String description = matcher.group(2).trim();

                        BigDecimal withdrawal = matcher.group(3).equals("-") ? null : new BigDecimal(matcher.group(3).replace(",",""));
                        BigDecimal deposit = matcher.group(4).equals("-") ? null : new BigDecimal(matcher.group(4).replace(",", ""));

                        BigDecimal amount = (withdrawal != null) ? withdrawal.negate() : (deposit != null) ? deposit : BigDecimal.ZERO;

                        samples.add(new TransactionSample(date, description, amount));
                        count++;
                    } catch (Exception e) {
                        logger.warn("Failed to parse DBS transaction line: {}", matcher.group(0));
                    }
                }

                break;
        }
    }
}
