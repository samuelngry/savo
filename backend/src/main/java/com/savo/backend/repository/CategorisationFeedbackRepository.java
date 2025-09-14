package com.savo.backend.repository;

import com.savo.backend.enums.FeedbackType;
import com.savo.backend.model.CategorisationFeedback;
import com.savo.backend.model.Category;
import com.savo.backend.model.Transaction;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategorisationFeedbackRepository extends JpaRepository<CategorisationFeedback, String> {

    List<CategorisationFeedback> findByUser(User user);
    List<CategorisationFeedback> findByTransaction(Transaction transaction);
    List<CategorisationFeedback> findByCategory(Category category);
    List<CategorisationFeedback> findByUserAndFeedbackType(User user, FeedbackType feedbackType);

    // Count how many times a particular category was corrected to another
    long countByOriginalCategoryIdAndCorrectedCategoryId(String originalCategoryId, String correctedCategoryId);

    // Check if user has ever submitted feedback for a transaction
    boolean existsByUserAndTransaction(User user, Transaction transaction);
}
