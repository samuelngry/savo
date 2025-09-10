package com.savo.backend.service;

import com.savo.backend.exception.ValidationException;
import com.savo.backend.model.BankAccount;
import com.savo.backend.repository.BankAccountRepository;
import com.savo.backend.repository.UserRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class BankDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(BankDetectionService.class);

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public BankDetectionService(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    public BankAccount detectAndResolveBankAccount(MultipartFile file, String userId) {
        try {
            String pdfText = extractTextFromPDF(file);

            BankDetectionResult detection = detectBankFromPDF(pdfText);

            Optional<BankAccount> existingAccount = bankAccountRepository.findByUserIdAndBankNameAndAccountNumberMasked(userId, detection.bankName, detection.maskedAccountNumber);

            if (existingAccount.isPresent()) {
                logger.info("Using existing bank account: bank={}, account={}", detection.bankName, detection.maskedAccountNumber);
                return existingAccount.get();
            } else {
                return createNewBankAccount(userId, detection);
            }
        } catch (IOException e) {
            logger.error("Failed to process PDF for bank detection", e);
            throw new ValidationException("Unable to read PDF for bank detection");
        }
    }

    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(1);
            return stripper.getText(document);
        }
    }

    private static class BankDetectionResult {
        String bankName;
        String accountType;
        String accountNumber;
        String maskedAccountNumber;
    }
}
