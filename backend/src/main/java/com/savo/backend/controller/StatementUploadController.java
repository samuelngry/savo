package com.savo.backend.controller;

import com.savo.backend.dto.statementupload.StatementUploadResponseDTO;
import com.savo.backend.exception.ValidationException;
import com.savo.backend.service.StatementUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/statements")
@CrossOrigin(origins = "*")
public class StatementUploadController {

    private static final Logger logger = LoggerFactory.getLogger(StatementUploadController.class);

    private final StatementUploadService statementUploadService;

    public StatementUploadController(StatementUploadService statementUploadService) {
        this.statementUploadService = statementUploadService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload bank statement",
            description = "Upload a PDF bank statement for processing",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Statement uploaded and processing started"),
                    @ApiResponse(responseCode = "400", description = "Invalid file or validation error"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "413", description = "File too large"),
                    @ApiResponse(responseCode = "415", description = "Unsupported media type")
            }
    )
    public ResponseEntity<StatementUploadResponseDTO> uploadStatement(
            @Parameter(description = "Bank statement PDF file", required = true)
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") @NotNull MultipartFile file) {

        try {
            String userId = userDetails.getUsername();
            logger.info("Statement upload request received: filename={}, size={}, user={}", file.getOriginalFilename(), file.getSize(), userId);

            validateUploadRequest(file);

            StatementUploadResponseDTO response = statementUploadService.processStatementUpload(file, userId);

            logger.info("Statement upload initiated successfully: uploadId={}", response.getId());

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        } catch (ValidationException e) {
            logger.warn("Statement upload validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during statement upload", e);
            throw new RuntimeException("Failed to process statement upload", e);
        }
    }

}
