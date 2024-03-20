package org.example;

import org.example.dao.MerchantDAO;
import org.example.dao.TransactionDAO;
import org.example.dao.UserDAO;
import org.example.entity.FraudFlagger;
import org.example.entity.Transaction;
import org.example.service.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class GuardianWatchApp {
    public static final String CSV_FILE_PATH = "src/main/resources/transactions.csv";
    public static final Logger LOGGER = Logger.getLogger(GuardianWatchApp.class.getName());

    public static void main(String[] args) {
        try {
            process();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method starts the application.
     * It initializes the DAOs and services, starts the task to update the median transaction amount for each user, reads transactions from the CSV file, detects fraudulent transactions, and sets merchants as fraudulent if a fraudulent transaction is detected.
     *
     * @throws IOException If there is an error reading the CSV file.
     */
    private static void process() throws IOException, InterruptedException {
        UserDAO userDAO = new UserDAO();
        MerchantDAO merchantDAO = new MerchantDAO();
        TransactionDAO transactionDAO = new TransactionDAO();

        ITransactionReaderService transactionReaderService = new TransactionReaderService(userDAO, merchantDAO, transactionDAO);
        IFraudDetectionService fraudDetectionService = new FraudDetectionService(userDAO, merchantDAO, transactionDAO);
        UserService userService = new UserService(userDAO, transactionDAO);

        userService.calculateMedianTransactionAmountForAllUser();

        Transaction transaction;
        while ((transaction = transactionReaderService.getNextTransaction()) != null) {

            LOGGER.info(String.format("Transaction: %s, %s, %s, %s\n", transaction.getUserId(), transaction.getAmount(), transaction.getTimestamp(), transaction.getMerchantName()));

            List<FraudFlagger> fraudFlaggerList = fraudDetectionService.detectFraud(transaction);
            if (!fraudFlaggerList.isEmpty()) {
                merchantDAO.setFraudulentCount(transaction.getMerchantName());
                StringBuilder response = new StringBuilder("Fraud detected: ");
                for (FraudFlagger flagger : fraudFlaggerList) {
                    response.append(flagger.toString()).append(" ");
                }
                System.out.println(response);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new InterruptedException();
            }
        }

        userService.stopCalculatingMedianTransactionAmount();
    }
}