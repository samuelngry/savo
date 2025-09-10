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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private BankDetectionResult detectBankFromPDF(String pdfText) throws IOException {
        BankDetectionResult result = new BankDetectionResult();

        if (pdfText.contains("DBS") || pdfText.contains("POSB")) {
            result.bankName = "DBS";
            result.accountNumber = extractAccountNumber(pdfText, "DBS");
        } else if (pdfText.contains("OCBC")) {
            result.bankName = "OCBC";
            result.accountNumber = extractAccountNumber(pdfText, "OCBC");
        } else if (pdfText.contains("UOB")) {
            result.bankName = "UOB";
            result.accountNumber = extractAccountNumber(pdfText, "UOB");
        } else {
            throw new ValidationException("Unsupported bank format. Currently supports DBS, OCBC and UOB");
        }

        // Detect account type
        if (pdfText.toLowerCase().contains("savings")) {
            result.accountType = "Savings";
        } else if (pdfText.toLowerCase().contains("current")) {
            result.accountType = "Current";
        } else if (pdfText.toLowerCase().contains("credit")) {
            result.accountType = "Credit Card";
        } else {
            result.accountType = "Savings"; // Default
        }

        result.maskedAccountNumber = maskAccountNumber(result.accountNumber);
        return result;
    }

    private String extractAccountNumber(String pdfText, String bankName) {
        Pattern pattern = null;
        Matcher matcher = null;

        switch (bankName) {
            case "DBS":
                pattern = Pattern.compile("\\b\\d{3}-\\d{5,6}-\\d{1}\\b");
                matcher = pattern.matcher(pdfText);
                if (matcher.find()) {
                    return matcher.group(0).replaceAll("-", "");
                }
                return "UNKNOWN";

            case "OCBC":
                pattern = Pattern.compile("Account No\\.?\\s*(\\d{10,12})", Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(pdfText);
                if (matcher.find()) {
                    return matcher.group(1);
                }
                return "UNKNOWN";

            case "UOB":
                pattern = Pattern.compile("Account Number:?\\s*(\\d{10,12})", Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(pdfText);
                if (matcher.find()) {
                    return matcher.group(1);
                }
                return "UNKNOWN";

            default:
                return "UNKNOWN";
        }
    }

    private static class BankDetectionResult {
        String bankName;
        String accountType;
        String accountNumber;
        String maskedAccountNumber;
    }
}
