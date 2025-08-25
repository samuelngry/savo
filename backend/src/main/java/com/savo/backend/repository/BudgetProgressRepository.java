package com.savo.backend.repository;

import com.savo.backend.model.Budget;
import com.savo.backend.model.BudgetProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetProgressRepository extends JpaRepository<BudgetProgress, String> {

    List<BudgetProgress> findByBudget(Budget budget);

    Optional<BudgetProgress> findByBudgetAndPeriodStartAndPeriodEnd(Budget budget, LocalDate periodStart, LocalDate periodEnd);

    Optional<BudgetProgress> findByBudgetAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(Budget budget, LocalDate today1, LocalDate today2);
}
