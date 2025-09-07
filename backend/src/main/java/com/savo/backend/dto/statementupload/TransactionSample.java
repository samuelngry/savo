package com.savo.backend.dto.statementupload;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionSample {
    private final LocalDate date;
    private final String description;
    private final BigDecimal amount;

    public TransactionSample(LocalDate date, String description, BigDecimal amount) {
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
