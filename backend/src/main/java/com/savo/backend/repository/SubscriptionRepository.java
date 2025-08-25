package com.savo.backend.repository;

import com.savo.backend.model.Subscription;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    List<Subscription> findByUser(User user);

    Optional<Subscription> findByUserAndServiceName(User user, String serviceName);

    List<Subscription> findByNextBillingDateBetween(LocalDate start, LocalDate end);

    List<Subscription> findByIsDuplicateTrue();

    List<Subscription> findByUserAndIsActiveTrue(User user);
}
