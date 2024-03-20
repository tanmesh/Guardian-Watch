package org.example.service;

import org.example.entity.FraudFlagger;
import org.example.entity.Transaction;

import java.util.List;

/**
 * This interface defines the methods that a Fraud Detection Service should implement.
 * Each method corresponds to a specific rule for detecting fraudulent transactions.
 */
public interface IFraudDetectionService {
    /**
     * Detects fraudulent activity in a transaction.
     * This method should implement the logic to check the transaction against various fraud detection rules.
     *
     * @param transaction The transaction to check.
     * @return A string describing the detected fraudulent activity, or null if no fraud is detected.
     */
    List<FraudFlagger> detectFraud(Transaction transaction);
}