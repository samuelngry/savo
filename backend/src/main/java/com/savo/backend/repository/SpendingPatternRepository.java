package com.savo.backend.repository;

import com.savo.backend.enums.PatternType;
import com.savo.backend.enums.TrendDirection;
import com.savo.backend.model.SpendingPattern;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SpendingPatternRepository extends JpaRepository<SpendingPattern, String> {

    List<SpendingPattern> findByUser(User user);
    List<SpendingPattern> findByUserAndIsActiveTrue(User user);
    List<SpendingPattern> findByUserAndPatternType(User user, PatternType patternType);
    List<SpendingPattern> findByUserAndTrendDirection(User user, TrendDirection trendDirection);

    // To recommend optimization
    List<SpendingPattern> findByUserAndPriorityScoreGreaterThanEqual(User user, BigDecimal priorityScore);
}
