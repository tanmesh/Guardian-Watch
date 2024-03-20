package org.example.service;

import org.example.dao.MerchantDAO;
import org.example.dao.TransactionDAO;
import org.example.dao.UserDAO;
import org.example.entity.Merchant;
import org.example.entity.Transaction;
import org.example.entity.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.GuardianWatchApp.CSV_FILE_PATH;

public class TransactionReaderService implements ITransactionReaderService {
    private final BufferedReader bufferedReader;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private UserDAO userDAO;
    private MerchantDAO merchantDAO;
    private TransactionDAO transactionDAO;

    public TransactionReaderService(UserDAO userDAO, MerchantDAO merchantDAO, TransactionDAO transactionDAO) throws IOException {
        this.bufferedReader = new BufferedReader(new FileReader(CSV_FILE_PATH));
        bufferedReader.readLine(); // Skip the header line
        this.userDAO = userDAO;
        this.merchantDAO = merchantDAO;
        this.transactionDAO = transactionDAO;
    }

    /**
     * Reads the next transaction from the CSV file.
     *
     * @return The next Transaction, or null if there are no more transactions.
     * @throws IOException If there is an error reading the CSV file.
     */
    @Override
    public Transaction getNextTransaction() throws IOException {
        String line = bufferedReader.readLine();
        if (line != null) {
            try {
                String[] values = line.split(",");

                String userId = values[0];
                double amount = Double.parseDouble(values[1]);
                LocalDateTime timestamp = LocalDateTime.parse(values[2], formatter);
                String merchantName = values[3];

                // create new merchant if it does not exist
                Merchant merchant = new Merchant(merchantName);
                merchantDAO.addMerchant(merchant);

                // todo: create new user if it does not exist
                User user = userDAO.getUser(userId);
                if (user == null) {
                    user = new User(userId);
                    userDAO.addUser(user);
                }

                Transaction transaction = new Transaction(userId, amount, timestamp, merchantName);

                transactionDAO.addTransaction(transaction);
                return transaction;
            } catch (IllegalArgumentException e) {
                // todo: replace with logger
                System.out.println("Error parsing transaction: " + e.getMessage());
                return null;
            } catch (Exception e) {
                System.out.println("Error reading transaction: " + e.getMessage());
                return null;
            }
        } else {
            bufferedReader.close();
            return null;
        }
    }
}