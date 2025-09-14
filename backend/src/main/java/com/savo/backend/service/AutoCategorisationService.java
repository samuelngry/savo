package com.savo.backend.service;

import com.savo.backend.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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
}
