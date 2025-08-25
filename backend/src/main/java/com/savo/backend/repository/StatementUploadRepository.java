package com.savo.backend.repository;

import com.savo.backend.enums.UploadStatus;
import com.savo.backend.model.StatementUpload;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatementUploadRepository extends JpaRepository<StatementUpload, String> {

    List<StatementUpload> findByUser(User user);
    List<StatementUpload> findByUploadStatus(UploadStatus uploadStatus);
}
