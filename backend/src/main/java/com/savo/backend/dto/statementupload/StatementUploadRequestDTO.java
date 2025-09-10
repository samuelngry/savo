package com.savo.backend.dto.statementupload;

import com.savo.backend.model.StatementUpload;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

public class StatementUploadRequestDTO {

    @NotNull(message = "File is required")
    private MultipartFile file;

    public StatementUploadRequestDTO() {}

    public StatementUploadRequestDTO(String bankAccountId, MultipartFile file) {
        this.file = file;
    }

    public boolean isValidFileType() {
        if (file == null || file.getOriginalFilename() == null) {
            return false;
        }
        String fileName = file.getOriginalFilename().toLowerCase();
        return fileName.endsWith(".pdf");
    }

    public boolean isValidFileSize(long maxSizeBytes) {
        return file != null && file.getSize() <= maxSizeBytes;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
