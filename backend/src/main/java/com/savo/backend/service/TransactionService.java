package com.savo.backend.service;

import com.savo.backend.dto.TransactionCreateDTO;
import com.savo.backend.dto.TransactionResponseDTO;
import com.savo.backend.dto.TransactionUpdateDTO;

import java.util.List;

public interface TransactionService {
    TransactionResponseDTO createTransaction(String userId, TransactionCreateDTO dto);
    List<TransactionResponseDTO> getUserTransactions(String userId);
    TransactionResponseDTO getTransaction(String userId, String transactionId);
    TransactionResponseDTO updateTransaction(String userId, String transactionId, TransactionUpdateDTO dto);
    void deleteTransaction(String userId, String transactionId);
}
