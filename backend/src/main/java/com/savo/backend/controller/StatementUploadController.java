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
import org.springframework.data.domain.Pageable;
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
}
