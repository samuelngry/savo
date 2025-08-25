package com.savo.backend.repository;

import com.savo.backend.enums.FeedbackType;
import com.savo.backend.model.CategorizationFeedback;
import com.savo.backend.model.Category;
import com.savo.backend.model.Transaction;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategorizationFeedbackRepository extends JpaRepository<CategorizationFeedback, String> {

    List<CategorizationFeedback> findByUser(User user);
    List<CategorizationFeedback> findByTransaction(Transaction transaction);
    List<CategorizationFeedback> findByCategory(Category category);
    List<CategorizationFeedback> findByUserAndFeedbackType(User user, FeedbackType feedbackType);

    // Count how many times a particular category was corrected to another
    long countByOriginalCategoryIdAndCorrectedCategoryId(String originalCategoryId, String correctedCategoryId);

    // Check if user has ever submitted feedback for a transaction
    boolean existsByUserAndTransaction(User user, Transaction transaction);
}
