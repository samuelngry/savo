package com.savo.backend.repository;

import com.savo.backend.model.Category;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<BudgetRepository, String> {

    List<BudgetRepository> findByUser(User user);

    Optional<BudgetRepository> findByUserAndCategoryAndIsActiveTrue(User user, Category category);

    List<BudgetRepository> findByUserAndIsActiveTrue(User user);

    List<BudgetRepository> findByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(User user, LocalDate today1, LocalDate today2);
}
