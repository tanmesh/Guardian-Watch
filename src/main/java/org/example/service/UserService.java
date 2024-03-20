package org.example.service;

import org.example.dao.TransactionDAO;
import org.example.dao.UserDAO;
import org.example.entity.Transaction;
import org.example.entity.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.example.GuardianWatchApp.LOGGER;

public class UserService {
    private UserDAO userDAO;
    private TransactionDAO transactionDAO;
    private Timer timer;

    public UserService(UserDAO userDAO, TransactionDAO transactionDAO) {
        this.userDAO = userDAO;
        this.transactionDAO = transactionDAO;
        this.timer = new Timer();
    }

    /**
     * Starts a task that updates the median transaction amount for each user every hour.
     */
    public void calculateMedianTransactionAmountForAllUser() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (User user : userDAO.getUserList()) {
                    LOGGER.info(String.format("Updating median for user: " + user.getUserId()));
                    List<Transaction> previousTransactions = transactionDAO.getPreviousTransactionsForAUser(LocalDateTime.now().minusMonths(6), user.getUserId());
                    if(!previousTransactions.isEmpty()) {
                        calculateMedianTransactionAmount(user.getUserId(), previousTransactions);
                    }
                }
            }
        };

        // Schedule the task to run every hour
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    /**
     * Stops the task that updates the median transaction amount for each user every hour.
     */
    public void stopCalculatingMedianTransactionAmount() {
        timer.cancel();
    }

    /**
     * Calculates the median transaction amount for a user based on their transactions in the last 6 months and
     * appropriately update the user's median transaction amount in the UserDAO.
     *
     * @param userId       The ID of the user.
     * @param transactions The list of transactions for the user in the last 6 months.
     */
    private void calculateMedianTransactionAmount(String userId, List<Transaction> transactions) {
        transactions.sort(Comparator.comparingDouble(Transaction::getAmount));
        double median;

        int size = transactions.size();
        if (size % 2 == 0) {
            double middle1 = transactions.get(size / 2 - 1).getAmount();
            double middle2 = transactions.get(size / 2).getAmount();
            median = (middle1 + middle2) / 2.0;
        } else {
            median = transactions.get(size / 2).getAmount();
        }

        userDAO.setMedianTransactionAmount(userId, median);
    }
}