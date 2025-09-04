package com.savo.backend.repository;

import com.savo.backend.enums.UploadStatus;
import com.savo.backend.model.StatementUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatementUploadRepository extends JpaRepository<StatementUpload, String> {

    List<StatementUpload> findByUserIdOrderByCreatedAtDesc(String userId);
    Page<StatementUpload> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    List<StatementUpload> findByBankAccountIdOrderByCreatedAtDesc(String bankAccountId);
    List<StatementUpload> findByUploadStatus(UploadStatus uploadStatus);
    Optional<StatementUpload> findByIdAndUserId(String id, String userId);
    Optional<StatementUpload> findByIdAndBankAccountId(String id, String bankAccountId);

    @Query("SELECT COUNT(su) FROM StatementUpload su WHERE su.user.id = :userId AND su.uploadStatus = :status")
    long countByUserIdAndStatus(@Param("userId") String userId, @Param("status") UploadStatus uploadStatus);

    long countByUserId(String userId);

    // Check if duplicate upload exists
    boolean existsByUserIdAndBankAccountIdAndFileNameAndCreatedAtAfter(String userId, String bankAccountId, String fileName, LocalDateTime after);

    // Find processing statements
    @Query("SELECT su FROM StatementUpload su WHERE su.uploadStatus = 'PROCESSING' AND su.processingStartedAt < :cutoffTime")
    List<StatementUpload> findStuckProcessingUploads(@Param("cutoffTime") LocalDateTime cutoffTime);

    // Find failed uploads
    @Query("SELECT su FROM StatementUpload su WHERE su.uploadStatus = 'FAILED' AND su.user.id = :userId ORDER BY su.createdAt DESC")
    List<StatementUpload> findFailedUploadsForUser(@Param("userId")  String userId);

    // Find recent uploads for a bank account (prevent too frequent uploads)
    @Query("SELECT su FROM StatementUpload su WHERE su.bankAccount.id = :bankAccountId AND su.createdAt > :since ORDER BY su.createdAt DESC")
    List<StatementUpload> findRecentUploadsForBankAccount(@Param("bankAccountId") String bankAccountId, @Param("since") LocalDateTime since);

    // Delete old failed uploads
    @Query("DELETE FROM StatementUpload su WHERE su.uploadStatus = 'FAILED' AND su.createdAt < :cutoffDate")
    void deleteFailedUploadsForUser(@Param("cutoffDate") LocalDateTime cutoffDate);
}
