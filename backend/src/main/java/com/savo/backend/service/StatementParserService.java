package com.savo.backend.service;

import com.savo.backend.model.StatementUpload;
import com.savo.backend.model.Transaction;
import com.savo.backend.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
