package com.savo.backend.service;

import com.savo.backend.enums.TransactionType;
import com.savo.backend.model.StatementUpload;
import com.savo.backend.model.Transaction;
import com.savo.backend.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StatementParserService {

    private static final Logger logger = LoggerFactory.getLogger(StatementParserService.class);

    private final FileStorageService fileStorageService;
    private final CategoryService categoryService;
    private final TransactionRepository transactionRepository;

    public StatementParserService(FileStorageService fileStorageService, CategoryService categoryService, TransactionRepository transactionRepository) {
        this.fileStorageService = fileStorageService;
        this.categoryService = categoryService;
        this.transactionRepository = transactionRepository;
    }

    public void parseAndSaveTransactions(StatementUpload upload) {
        try {
            logger.info("Starting statement processing for upload: {}", upload.getId());

            String presignedUrl = fileStorageService.getPresignedDownloadUrl(upload.getS3Key());
            String pdfText = extractPDFTextFromUrl(presignedUrl);

            // Parse based on bank
            List<Transaction> transactions = parseTransactionsByBank(
                    pdfText,
                    upload.getBankAccount().getBankName(),
                    upload
            );

            List<Transaction> savedTransactions = transactionRepository.saveAll(transactions);

            upload.setTotalTransactionsExtracted(savedTransactions.size());

            logger.info("Successfully parsed {} transactions for upload: {}", savedTransactions.size(), upload.getId());

        } catch (Exception e) {
            logger.error("Failed to parse transactions for upload: {}", upload.getId(), e);
            throw new RuntimeException("Failed to parse transactions for upload: " + upload.getId(), e);
        }
    }

    private List<Transaction> parseTransactionsByBank(String pdfText, String bankName, StatementUpload upload) {
        switch (bankName.toUpperCase()) {
            case "DBS":
                return parseDBSTransactions(pdfText, upload);
            case "OCBC":
                return parseOCBCTransactions(pdfText, upload);
            case "UOB":
                return parseUOBTransactions(pdfText, upload);
            default:
                throw new IllegalArgumentException("Unsupported bank: " + bankName);
        }
    }

    // TODO: add each banks transaction pattern format to parse
    private List<Transaction> parseDBSTransactions(String pdfText, StatementUpload upload) {
        List<Transaction> transactions = new ArrayList<>();

        Pattern transactionPattern = Pattern.compile(
                "(\\d{2}/\\d{2}/\\d{4})\\s+" +
                        "(.+?)\\s+" +
                        "(\\d{1,3}(?:,\\d{3})*\\.\\d{2})?" +
                        "\\s+" +
                        "(\\d{1,3}(?:,\\d{3})*\\.\\d{2})?" +
                        "(?:\\s+(\\d{1,3}(?:,\\d{3})*\\.\\d{2}))?",
                Pattern.MULTILINE
        );

        Matcher matcher = transactionPattern.matcher(pdfText);

        while (matcher.find()) {
            try {
                Transaction transaction = new Transaction();

                String dateStr = matcher.group(1);
                LocalDate transactionDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                transaction.setTransactionDate(transactionDate);

                String description = matcher.group(2);
                transaction.setDescription(description);
                transaction.setMerchantName(extractMerchantName(description, "DBS"));

                String debitStr = matcher.group(3);
                String creditStr = matcher.group(4);
                String balanceStr = matcher.group(5);

                BigDecimal amount;
                TransactionType type;

                if (debitStr != null && !debitStr.trim().isEmpty()) {
                    amount = new BigDecimal(debitStr.replace(",", ""));
                    type = TransactionType.Debit;
                } else if (creditStr != null && !creditStr.trim().isEmpty()) {
                    amount = new BigDecimal(creditStr.replace(",", ""));
                    type = TransactionType.Credit;
                } else {
                    logger.warn("Transaction with no amount found, skipping: {}", matcher.group(0));
                    continue;
                }

                transaction.setAmount(amount);
                transaction.setTransactionType(type);

                if (balanceStr != null && !balanceStr.trim().isEmpty()) {
                    transaction.setBalanceAfter(new BigDecimal(balanceStr.replace(",", "")));
                }

                setTransactionMetadate(transaction, upload);

                transactions.add(transaction);
            } catch (Exception e) {
                logger.warn("Failed to parse DBS transaction line: '{}', error: {}", matcher.group(0), e.getMessage());
            }
        }

        return transactions;
    }

    private List<Transaction> parseOCBCTransactions(String pdfText, StatementUpload upload) {
        List<Transaction> transactions = new ArrayList<>();

        int statementYear = extractStatementYear(pdfText, "OCBC");

        Pattern transactionPattern = Pattern.compile(
                "(\\d{2}\\s+\\w{3})\\s+" +
                        "(\\d{2}\\s+\\w{3})\\s+" +
                        "(.+?)\\s+" +
                        "(?:(\\S+)\\s+)?" +
                        "(\\d{1,3}(?:,\\d{3})*\\.\\d{2})?\\s+" +
                        "(\\d{1,3}(?:,\\d{3})*\\.\\d{2})?\\s+" +
                        "(\\d{1,3}(?:,\\d{3})*\\.\\d{2})",
                Pattern.MULTILINE
        );

        Matcher matcher = transactionPattern.matcher(pdfText);

        while (matcher.find()) {
            try {
                Transaction transaction = new Transaction();

                String dateStr = matcher.group(1) + " " + statementYear;
                LocalDate transactionDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH));
                transaction.setTransactionDate(transactionDate);

                String description = matcher.group(3);
                transaction.setDescription(description);
                transaction.setMerchantName(extractMerchantName(description, "OCBC"));

                String debitStr = matcher.group(5);
                String creditStr = matcher.group(6);
                String balanceStr = matcher.group(7);

                BigDecimal amount;
                TransactionType type;

                if (debitStr != null && !debitStr.trim().isEmpty()) {
                    amount = new BigDecimal(debitStr.replace(",", ""));
                    type = TransactionType.Debit;
                } else if (creditStr != null && !creditStr.trim().isEmpty()) {
                    amount = new BigDecimal(creditStr.replace(",", ""));
                    type = TransactionType.Credit;
                } else {
                    continue;
                }

                transaction.setAmount(amount);
                transaction.setTransactionType(type);
                transaction.setBalanceAfter(new BigDecimal(balanceStr.replace(",", "")));

                setTransactionMetadata(transaction, upload);
                transactions.add(transaction);

            } catch (Exception e) {
                logger.warn("Failed to parse OCBC transaction line: '{}', error: {}", matcher.group(0), e.getMessage());
            }
        }

        return transactions;
    }

    private List<Transaction> parseUOBTransactions(String pdfText, StatementUpload upload) {
        List<Transaction> transactions = new ArrayList<>();

        Pattern transactionPattern = Pattern.compile(
                "(\\d{2}/\\d{2}/\\d{4})\\s+" +
                        "(\\d{2}/\\d{2}/\\d{4})\\s+" +
                        "(\\d{2}/\\d{2}/\\d{4}\\s+\\d{2}:\\d{2}:\\d{2}\\s+[AP]M)\\s+" +
                        "(.+?)\\s+" +
                        "(\\d{1,3}(?:,\\d{3})*\\.\\d{2})\\s+" +
                        "(\\d{1,3}(?:,\\d{3})*\\.\\d{2})\\s+" +
                        "(\\d{1,3}(?:,\\d{3})*\\.\\d{2})",
                Pattern.MULTILINE
        );

        Matcher matcher = transactionPattern.matcher(pdfText);

        while (matcher.find()) {
            try {
                Transaction transaction = new Transaction();

                String dateStr = matcher.group(1);
                LocalDate transactionDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                transaction.setTransactionDate(transactionDate);

                String description = matcher.group(4).replaceAll("\\s+", " ").trim();
                transaction.setDescription(description);
                transaction.setMerchantName(extractMerchantName(description, "UOB"));

                String creditStr = matcher.group(5);
                String debitStr = matcher.group(6);
                String balanceStr = matcher.group(7);

                BigDecimal amount;
                TransactionType type;

                BigDecimal credit = new BigDecimal(creditStr.replace(",", ""));
                BigDecimal debit = new BigDecimal(debitStr.replace(",", ""));

                if (debit.compareTo(BigDecimal.ZERO) > 0) {
                    amount = debit;
                    type = TransactionType.Debit;
                } else if (credit.compareTo(BigDecimal.ZERO) > 0) {
                    amount = credit;
                    type = TransactionType.Credit;
                } else {
                    continue;
                }

                transaction.setAmount(amount);
                transaction.setTransactionType(type);
                transaction.setBalanceAfter(new BigDecimal(balanceStr.replace(",", "")));

                setTransactionMetadata(transaction, upload);
                transactions.add(transaction);
            } catch (Exception e) {
                logger.warn("Failed to parse UOB transaction line: '{}', error: {}", matcher.group(0), e.getMessage());
            }
        }

        return transactions;
    }
}
