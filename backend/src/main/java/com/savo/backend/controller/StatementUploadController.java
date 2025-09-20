package com.savo.backend.controller;

import com.savo.backend.dto.statementupload.*;
import com.savo.backend.exception.ValidationException;
import com.savo.backend.service.StatementUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/users/statements")
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

    @GetMapping("/{uploadId}/status")
    @Operation(
            summary = "Get upload status",
            description = "Get the current status of a statement upload and processing",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Upload not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<UploadStatusResponseDTO> getUploadStatus(
            @Parameter(description = "Upload ID", required = true)
            @PathVariable String uploadId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            String userId = userDetails.getUsername();
            logger.info("Get upload status request received: uploadId={}", uploadId);

            UploadStatusResponseDTO status = statementUploadService.getUploadStatus(uploadId, userId);

            return ResponseEntity.ok(status);

        } catch (ValidationException e) {
            logger.warn("Upload status request failed: uploadId={}, error={}", uploadId, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/history")
    @Operation(
            summary = "Get upload history",
            description = "Get paginated list of user's statement uploads with their status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Upload history retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<Page<UploadHistoryResponseDTO>> getUploadHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable,
            @Parameter(description = "Filter by bank name")
            @RequestParam(required = false) String bankName,
            @Parameter(description = "Filter by upload status")
            @RequestParam(required = false) String status) {

        try {
            String userId = userDetails.getUsername();

            Page<UploadHistoryResponseDTO> history = statementUploadService.getUploadHistory(
                    userId,
                    pageable,
                    bankName,
                    status
            );

            return ResponseEntity.ok(history);

        } catch (Exception e) {
            logger.error("Failed to retrieve upload history for user: {}", userDetails.getUsername(), e);
            throw new RuntimeException("Failed to retrieve upload history", e);
        }
    }

    @DeleteMapping("/{uploadId}")
    @Operation(
            summary = "Cancel/Delete upload",
            description = "Cancel a pending upload or delete a completed upload and its transactions",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Upload deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Upload not found"),
                    @ApiResponse(responseCode = "400", description = "Upload cannot be deleted"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<Void> deleteUpload(
            @Parameter(description = "Upload ID", required = true)
            @RequestParam String uploadId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            String userId = userDetails.getUsername();

            statementUploadService.deleteUpload(uploadId, userId);
            logger.info("Upload deleted successfully: uploadId={}, user={}", uploadId, userId);

            return ResponseEntity.noContent().build();

        } catch (ValidationException e) {
            logger.warn("Upload validation failed: uploadId={}, error={}", uploadId, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/{uploadId}/retry")
    @Operation(
            summary = "Retry failed upload",
            description = "Retry processing a failed statement upload",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Retry initiated successfully"),
                    @ApiResponse(responseCode = "404", description = "Upload not found"),
                    @ApiResponse(responseCode = "400", description = "Upload cannot be retried"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<StatementUploadResponseDTO> retryUpload(
            @Parameter(description = "Upload ID", required = true)
            @PathVariable String uploadId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            String userId = userDetails.getUsername();

            StatementUploadResponseDTO response = statementUploadService.retryUpload(
                    uploadId,
                    userId
            );

            logger.info("Upload retry successfully: uploadId={}", uploadId);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        } catch (ValidationException e) {
            logger.warn("Upload validation failed: uploadId={}, error={}", uploadId, e.getMessage());
            throw e;
        }
    }

    private void validateUploadRequest(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File is required and cannot be empty");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new ValidationException("Only PDF files are supported");
        }

        if (file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
            throw new ValidationException("File must have a valid filename");
        }

        long maxSize = 50 * 1024 * 1024; // 50MB
        if (file.getSize() > maxSize) {
            throw new ValidationException("File size cannot exceed 50MB");
        }
    }
}