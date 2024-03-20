package org.example.dao;

import lombok.Getter;
import lombok.Setter;
import org.example.entity.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TransactionDAO {
    private List<Transaction> transactionList = new ArrayList<>();

    /**
     * This method adds a new transaction to the transaction list.
     *
     * @param transaction The Transaction object to be added.
     */
    public void addTransaction(Transaction transaction) {
        transactionList.add(transaction);
    }

    /**
     * This method retrieves a list of previous transactions for a user within a certain time window.
     * It does this by iterating through the transaction list and adding transactions that match the user ID and are within the time window to a new list.
     *
     * @param timeWindow The time window within which to retrieve transactions.
     * @param userId     The ID of the user for whom to retrieve transactions.
     * @return A list of Transaction objects that match the given user ID and are within the time window.
     */
    public List<Transaction> getPreviousTransactionsForAUser(LocalDateTime timeWindow, String userId) {
        List<Transaction> previousTransactions = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if (transaction.getUserId().equals(userId) && transaction.getTimestamp().minusSeconds(timeWindow.getSecond()).isBefore(transaction.getTimestamp())) {
                previousTransactions.add(transaction);
            }
        }
        return previousTransactions;
    }
}
