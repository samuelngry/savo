package com.savo.backend.repository;

import com.savo.backend.enums.UploadStatus;
import com.savo.backend.model.StatementUpload;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StatementUploadRepository extends JpaRepository<StatementUpload, String> {

    List<StatementUpload> findByUserIdOrderByCreatedAtDesc(String userId);
    List<StatementUpload> findByBankAccountIdOrderByCreatedAtDesc(String bankAccountId);
    List<StatementUpload> findByUploadStatus(UploadStatus uploadStatus);
    Optional<StatementUpload> findByIdAndUserId(String id, String userId);
    Optional<StatementUpload> findByIdAndBankAccountId(String id, String bankAccountId);

    @Query("SELECT COUNT(su) FROM StatementUpload su WHERE su.user.id = :userId AND su.uploadStatus = :status")
    long countByUserIdAndStatus(@Param("userId") String userId, @Param("status") UploadStatus uploadStatus);

}
