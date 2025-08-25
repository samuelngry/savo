package com.savo.backend.repository;

import com.savo.backend.enums.ModelType;
import com.savo.backend.model.MLModelVersion;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MLModelVersionRepository extends JpaRepository<MLModelVersion, String> {

    List<MLModelVersion> findByUser(User user);

    Optional<MLModelVersion> findByVersion(String version);

    List<MLModelVersion> findByModelType(ModelType modelType);
    List<MLModelVersion> findByIsActiveTrue();

    Optional<MLModelVersion> findFirstByModelTypeOrderByDeployedAtDesc(ModelType modelType);

    Optional<MLModelVersion> findFirstByModelTypeOrderByAccuracyScoreDesc(ModelType modelType);

    List<MLModelVersion> findByModelTypeAndIsActiveTrue(ModelType modelType);
}
