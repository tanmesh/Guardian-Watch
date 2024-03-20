package org.example.service;

import org.example.dao.MerchantDAO;
import org.example.dao.TransactionDAO;
import org.example.dao.UserDAO;
import org.example.entity.FraudFlagger;
import org.example.entity.Transaction;
import org.example.entity.User;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class FraudDetectionService implements IFraudDetectionService {
    public static final int HIGH_AMOUNT_THRESHOLD = 10;
    public static final int MAX_TRANSACTION_COUNT_PER_MERCHANT_PER_DAY = 10;
    public static final int MAX_TRANSACTION_COUNT_PER_MINUTE = 3;
    public static final int MAX_TRANSACTION_COUNT_PER_HOUR = 5;
    public static final int FRAUDULENT_MERCHANT_THRESHOLD = 10;
    public UserDAO userDAO;
    public MerchantDAO merchantDAO;
    public TransactionDAO transactionDAO;

    public FraudDetectionService(UserDAO userDAO, MerchantDAO merchantDAO, TransactionDAO transactionDAO) {
        this.merchantDAO = merchantDAO;
        this.userDAO = userDAO;
        this.transactionDAO = transactionDAO;
    }

    /**
     * This method detects fraudulent transactions based on certain criteria.
     *
     * @param transaction The transaction to be checked.
     * @return A string indicating the reason for the transaction being fraudulent, or an empty string if it is not fraudulent.
     */
    @Override
    public List<FraudFlagger> detectFraud(Transaction transaction) {
        boolean highAmountTransaction = isHighAmountTransaction(transaction);
        boolean oddTimeTransaction = isOddTimeTransaction(transaction); // in extension
        boolean tooManyTransactionAcrossMerchant = isTooManyTransactionAcrossMerchant(transaction);
        boolean tooManyTransactionWithSameMerchant = isTooManyTransactionWithSameMerchant(transaction);
        boolean fraudulentMerchant = isFraudulentMerchant(transaction);

        List<FraudFlagger> fraudFlaggers = new ArrayList<>();
        if (highAmountTransaction) fraudFlaggers.add(FraudFlagger.HIGH_AMOUNT_TRANSACTION);
        if (oddTimeTransaction) fraudFlaggers.add(FraudFlagger.ODD_TIME_TRANSACTION);
        if (tooManyTransactionWithSameMerchant) fraudFlaggers.add(FraudFlagger.TOO_MANY_TRANSACTION_WITH_SAME_MERCHANT);
        if (tooManyTransactionAcrossMerchant && !tooManyTransactionWithSameMerchant) fraudFlaggers.add(FraudFlagger.TOO_MANY_TRANSACTION_ACROSS_MERCHANT);
        if (fraudulentMerchant) fraudFlaggers.add(FraudFlagger.FRAUDULENT_MERCHANT);
        return fraudFlaggers;
    }

    /**
     * This method checks if a transaction amount is unusually high or low.
     * It does this by comparing the transaction amount with the median of all previous transactions of the user.
     *
     * @param transaction The transaction to be checked.
     * @return A boolean indicating whether the transaction amount is unusually high or low.
     */
    boolean isHighAmountTransaction(Transaction transaction) {
        String userId = transaction.getUserId();
        User user = userDAO.getUser(userId);
        return transaction.getAmount() > user.getMedianTransactionAmount() * HIGH_AMOUNT_THRESHOLD || transaction.getAmount() < user.getMedianTransactionAmount() / HIGH_AMOUNT_THRESHOLD;
    }

    /**
     * This method checks if a transaction was made at an odd time.
     * It does this by checking if the transaction time is between 2 PM and 6 AM.
     *
     * @param transaction The transaction to be checked.
     * @return A boolean indicating whether the transaction was made at an odd time.
     */
    boolean isOddTimeTransaction(Transaction transaction) {
        LocalTime transactionTime = LocalTime.from(transaction.getTimestamp());

        LocalDateTime startTime = LocalDateTime.now().withHour(2).withMinute(0).withSecond(0);
        LocalDateTime endTime = LocalDateTime.now().withHour(6).withMinute(0).withSecond(0);

        // Check if the transaction time is between 2 PM and 6 AM
        return (transactionTime.isAfter(LocalTime.from(startTime)) && transactionTime.isBefore(LocalTime.from(endTime)));
    }

    /**
     * This method checks if a user is making transactions too frequently.
     * It does this by checking the number of transactions made by the user in the last minute and the last hour.
     * If the user has made 3 or more transactions in the last minute or 5 or more transactions in the last hour, the user is considered to be making transactions too frequently.
     *
     * @param transaction The transaction to be checked.
     * @return A boolean indicating whether the user is making transactions too frequently.
     */
    boolean isTooManyTransactionAcrossMerchant(Transaction transaction) {
        List<Transaction> numberOfTransactionsInLastOneMinute = transactionDAO.getPreviousTransactionsForAUser(LocalDateTime.now().minusMinutes(1), transaction.getUserId()); // last 1 minute
        if (numberOfTransactionsInLastOneMinute.size() >= MAX_TRANSACTION_COUNT_PER_MINUTE) { // 3 transactions
            return true;
        }

        List<Transaction> numberOfTransactionsInLastOneHour = transactionDAO.getPreviousTransactionsForAUser(LocalDateTime.now().minusHours(1), transaction.getUserId()); // last 1 hour
        return numberOfTransactionsInLastOneHour.size() >= MAX_TRANSACTION_COUNT_PER_HOUR; // 5 transactions
    }

    /**
     * This method checks if a transaction was made with a fraudulent merchant.
     * It does this by checking if the merchant associated with the transaction is marked as fraudulent.
     *
     * @param transaction The transaction to be checked.
     * @return A boolean indicating whether the transaction was made with a fraudulent merchant.
     */
    boolean isFraudulentMerchant(Transaction transaction) {
        return merchantDAO.getFraudulentCount(transaction.getMerchantName()) >= FRAUDULENT_MERCHANT_THRESHOLD;
    }

    /**
     * This method checks if a user is making too many transactions with the same merchant.
     * It does this by checking the number of transactions made by the user with the same merchant in the last day.
     *
     * @param transaction The transaction to be checked.
     * @return A boolean indicating whether the user is making too many transactions with the same merchant.
     */
    boolean isTooManyTransactionWithSameMerchant(Transaction transaction) {
        List<Transaction> lastOneDayTransactions = transactionDAO.getPreviousTransactionsForAUser(LocalDateTime.now().minusDays(1), transaction.getUserId());
        long count = lastOneDayTransactions.stream().filter(t -> t.getMerchantName().equals(transaction.getMerchantName())).count();
        return count >= MAX_TRANSACTION_COUNT_PER_MERCHANT_PER_DAY;
    }
}

