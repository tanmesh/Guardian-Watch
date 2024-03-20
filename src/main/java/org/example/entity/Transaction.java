package org.example.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Transaction {
    private String userId;
    private Double amount;
    private LocalDateTime timestamp;
    private String merchantName;

    public Transaction(String userId, Double amount, LocalDateTime timestamp, String merchantName) {
        this.userId = userId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.merchantName = merchantName;
    }
}