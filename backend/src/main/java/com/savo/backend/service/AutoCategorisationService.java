package com.savo.backend.service;

import com.savo.backend.model.Category;
import com.savo.backend.model.Transaction;
import com.savo.backend.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AutoCategorisationService {

    private static final Logger logger = LoggerFactory.getLogger(AutoCategorisationService.class);
    private final CategoryRepository categoryRepository;

    private static final Map<String, String[]> CATEGORY_KEYWORDS = new HashMap<String, String[]>() {{
        put("Food & Drinks", new String[]{
                "MCDONALD", "KFC", "BURGER KING", "SUBWAY", "PIZZA HUT", "DOMINO",
                "STARBUCKS", "COFFEE BEAN", "YA KUN", "TOAST BOX", "KOPITIAM",
                "HAWKER", "FOOD COURT", "COFFEE", "CAFE", "RESTAURANT", "BISTRO",
                "DELIVEROO", "FOODPANDA", "GRAB FOOD", "KITCHEN", "DINING",
                "BREAD TALK", "FOUR FINGERS", "SWENSEN", "CARL JR", "EATERY"
        });

        // Subcategory of food
        put("Groceries", new String[]{
                "NTUC", "FAIRPRICE", "COLD STORAGE", "GIANT", "SHENG SIONG",
                "PRIME SUPERMARKET", "MARKETPLACE", "FRESH MART", "GROCERY",
                "SUPERMARKET", "MARKET", "WET MARKET", "PROVISION"
        });

        put("Transport", new String[]{
                "EZ-LINK", "EZ LINK", "EZLINK", "TRANSIT LINK", "MRT", "BUS",
                "GRAB", "GOJEK", "TAXI", "COMFORT", "PREMIER TAXI", "TRANS CAB",
                "SHELL", "ESSO", "CALTEX", "MOBIL", "SINOPEC", "PETROL", "FUEL",
                "PARKING", "CARPARK", "ERP", "ROAD TAX", "INSURANCE VEHICLE",
                "WORKSHOP", "SERVICING", "COE", "AUTOMOBILE"
        });

        put("Shopping", new String[]{
                "SHOPEE", "LAZADA", "AMAZON", "QOO10", "CAROUSEL", "ZALORA",
                "UNIQLO", "H&M", "ZARA", "COTTON ON", "CHARLES KEITH",
                "TAKASHIMAYA", "ION ORCHARD", "VIVOCITY", "JURONG POINT",
                "WESTGATE", "PLAZA SINGAPURA", "BUGIS JUNCTION", "SHOPPING",
                "MALL", "DEPARTMENT STORE", "FASHION", "CLOTHING", "SHOES"
        });

        put("Bills & Utilities", new String[]{
                "SINGTEL", "STARHUB", "M1", "CIRCLES LIFE", "GIGA", "TELCO",
                "SP GROUP", "SP SERVICES", "PUB", "UTILITIES", "ELECTRICITY",
                "WATER", "GAS", "TOWN COUNCIL", "CONSERVANCY", "S&CC",
                "GIRO", "AXS", "SAM", "GOVERNMENT", "IRAS", "CPF", "HDB",
                "INSURANCE", "POLICY", "PREMIUM", "BILL PAYMENT"
        });

        put("Entertainment", new String[]{
                "NETFLIX", "DISNEY PLUS", "AMAZON PRIME", "SPOTIFY", "APPLE MUSIC",
                "YOUTUBE PREMIUM", "HBO", "VIKI", "STEAM", "PLAYSTATION", "XBOX",
                "CINEMA", "CATHAY", "GOLDEN VILLAGE", "SHAW", "MOVIE", "FILM",
                "SENTOSA", "USS", "ZOO", "BIRD PARK", "GARDENS BY THE BAY",
                "ART SCIENCE MUSEUM", "NATIONAL MUSEUM", "ENTERTAINMENT",
                "GAMING", "ARCADE", "BOWLING", "KTV", "KARAOKE"
        });

        put("Healthcare", new String[]{
                "CLINIC", "POLYCLINIC", "HOSPITAL", "SGH", "NUH", "TTSH", "CGH",
                "MEDICAL", "DOCTOR", "DENTIST", "DENTAL", "PHARMACY", "GUARDIAN",
                "WATSONS", "UNITY", "MEDICINE", "PRESCRIPTION", "HEALTH",
                "PHYSIOTHERAPY", "SPECIALIST", "CONSULTATION", "TREATMENT",
                "INSURANCE MEDICAL", "MEDISAVE"
        });

        put("Salary", new String[]{
                "SALARY", "PAYROLL", "WAGES", "PAY", "EMPLOYER", "CPF BOARD",
                "BONUS", "ALLOWANCE", "COMMISSION", "OVERTIME"
        });

        put("Investment", new String[]{
                "DIVIDEND", "INTEREST", "INVESTMENT", "STOCK", "BOND", "UNIT TRUST",
                "ROBO ADVISOR", "TRADING", "BROKERAGE", "DBS VICKERS", "POEMS",
                "TIGER BROKERS", "MOOMOO", "SYFE", "STASHAWAY", "ENDOWUS"
        });

        put("Education", new String[]{
                "SCHOOL", "UNIVERSITY", "COLLEGE", "TUITION", "COURSE", "TRAINING",
                "UDEMY", "COURSERA", "SKILLSFUTURE", "BOOK", "STATIONERY",
                "POPULAR BOOKSTORE", "TIMES", "EDUCATION", "LEARNING"
        });

        put("Personal Care", new String[]{
                "SALON", "BARBER", "HAIR", "NAIL", "MASSAGE", "SPA", "FACIAL",
                "BEAUTY", "COSMETICS", "SEPHORA", "MAKEUP", "SKINCARE",
                "PERFUME", "PERSONAL CARE", "HYGIENE"
        });
    }};

    public AutoCategorisationService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public String autoCategoriseTransaction(Transaction transaction) {
        String description = transaction.getDescription().toUpperCase();
        String merchantName = transaction.getMerchantName() != null ?
                transaction.getMerchantName().toUpperCase() : null;

        String fullText = (description + " " + merchantName).toUpperCase();

        if (transaction.getTransactionType().toString().equals("Credit")) {
            for (String keyword : CATEGORY_KEYWORDS.get("Salary")) {
                if (fullText.contains(keyword)) {
                    return findOrCreateCategory("Salary", transaction.getUser().getId(), true);
                }
            }

            for (String keyword : CATEGORY_KEYWORDS.get("Investment")) {
                if (fullText.contains(keyword)) {
                    return findOrCreateCategory("Investment", transaction.getUser().getId(), true);
                }
            }

            return findOrCreateCategory("Other Income", transaction.getUser().getId(), true);
        }

        // Find most likely category for expense transaction
        double maxScore = 0.0;
        String bestCategory = null;

        for (Map.Entry<String, String[]> entry :CATEGORY_KEYWORDS.entrySet()) {
            String categoryName = entry.getKey();

            if (categoryName.equals("Salary") || categoryName.equals("Investment")) {
                continue;
            }

            double score = calculateCategoryScore(fullText, entry.getValue());
            if (score > maxScore) {
                maxScore = score;
                bestCategory = categoryName;
            }
        }

        if (bestCategory != null && maxScore > 0.5) {
            return findOrCreateCategory(bestCategory, transaction.getUser().getId(), false);
        }

        String amountBasedCategory = categoriseByAmount(transaction.getAmount());
        if (amountBasedCategory != null) {
            return findOrCreateCategory(amountBasedCategory, transaction.getUser().getId(), false);
        }

        return findOrCreateCategory("Uncategorised", transaction.getUser().getId(), false);
    }

    private double calculateCategoryScore(String text, String[] keywords) {
        int matches = 0;
        double totalScore = 0.0;

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                matches++;
                // Longer keywords get higher scores (more specific)
                totalScore += Math.min(1.0, keyword.length() / 10.0);
            }
        }

        // Normalise score based on number of matches and keyword strength
        return matches > 0 ? totalScore / keywords.length : 0.0;
    }

    private String categoriseByAmount(BigDecimal amount) {
        BigDecimal absAmount = amount.abs();

        if (absAmount.compareTo(new BigDecimal("5")) <= 0) {
            return "Transport";
        }

        if (absAmount.compareTo(new BigDecimal("20")) <= 0) {
            return "Food & Dining";
        }

        if (absAmount.compareTo(new BigDecimal("100")) <= 0) {
            return "Groceries";
        }

        if (absAmount.compareTo(new BigDecimal("500")) <= 0) {
            return "Shopping";
        }

        return "Bills & Utilities";
    }

    private String findOrCreateCategory(String categoryName, String userId, boolean isIncome) {
        Optional<Category> systemCategory = categoryRepository.findByNameAndUserIdIsNull(categoryName);
        if (systemCategory.isPresent()) {
            return systemCategory.get().getId();
        }

        Optional<Category> userCategory = categoryRepository.findByNameAndUserId(categoryName, userId);
        if (userCategory.isPresent()) {
            return userCategory.get().getId();
        }

        // Create new system category
        return createSystemCategory(categoryName, isIncome, getDefaultIcon(categoryName), getDefaultColor(categoryName));
    }

    @Transactional
    private String createSystemCategory(String categoryName, boolean isIncome, String icon, String color) {
        Category category = new Category();
        category.setName(categoryName);
        category.setUser(null);
        category.setIcon(icon);
        category.setColor(color);
        category.setIncomeCategory(isIncome);
        category.setActive(true);
        category.setCreatedAt(LocalDateTime.now());

        Category saved = categoryRepository.save(category);
        return saved.getId();
    }
}
