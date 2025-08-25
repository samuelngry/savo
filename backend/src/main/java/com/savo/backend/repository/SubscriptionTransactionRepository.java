package com.savo.backend.repository;

import com.savo.backend.model.Subscription;
import com.savo.backend.model.SubscriptionTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionTransactionRepository extends JpaRepository<SubscriptionTransaction, String> {

    List<SubscriptionTransaction> findBySubscription(Subscription subscription);

    Optional<SubscriptionTransaction> findTopBySubscriptionOrderByBillingPeriodDesc();

    Long countBySubscriptionAndWasPredicted(Subscription subscription, boolean wasPredicted);

    List<SubscriptionTransaction> findByWasPredictedFalse();
}
