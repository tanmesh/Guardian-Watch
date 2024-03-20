package org.example.service;

import org.example.GuardianWatchApp;
import org.example.dao.MerchantDAO;
import org.example.dao.TransactionDAO;
import org.example.dao.UserDAO;
import org.example.entity.FraudFlagger;
import org.example.entity.Merchant;
import org.example.entity.Transaction;
import org.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class FraudDetectionServiceTest {
    private FraudDetectionService fraudDetectionService;
    private UserDAO userDAO;
    private MerchantDAO merchantDAO;
    private TransactionDAO transactionDAO;
    public static final Logger LOGGER = Logger.getLogger(GuardianWatchApp.class.getName());

    /**
     * This method sets up the test environment before each test.
     * It initializes the FraudDetectionService and mocks the DAOs.
     */
    @BeforeEach
    public void setUp() {
        userDAO = Mockito.mock(UserDAO.class);
        merchantDAO = Mockito.mock(MerchantDAO.class);
        transactionDAO = Mockito.mock(TransactionDAO.class);
        fraudDetectionService = new FraudDetectionService(userDAO, merchantDAO, transactionDAO);
    }

    /**
     * This test checks if the FraudDetectionService correctly identifies high amount transactions as fraudulent.
     */
    @Test
    public void highAmountTransactionTest() {
        User user = new User("user1");
        user.setMedianTransactionAmount(200.0);
        when(userDAO.getUser("user1")).thenReturn(user);

        {
            Transaction transaction = new Transaction("user1", 5000.0, LocalDateTime.now(), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            LOGGER.info(String.format(fraudFlaggers.toString()));
            assertTrue(fraudFlaggers.contains(FraudFlagger.HIGH_AMOUNT_TRANSACTION));
        }

        {
            Transaction transaction = new Transaction("user1", 10.0, LocalDateTime.now(), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            LOGGER.info(String.format(fraudFlaggers.toString()));
            assertTrue(fraudFlaggers.contains(FraudFlagger.HIGH_AMOUNT_TRANSACTION));
        }

        {
            Transaction transaction = new Transaction("user1", 150.0, LocalDateTime.now(), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            assertFalse(fraudFlaggers.contains(FraudFlagger.HIGH_AMOUNT_TRANSACTION));
        }
    }

    /**
     * This test checks if the FraudDetectionService correctly identifies transactions made at odd times as fraudulent.
     */
    @Test
    public void oddTimeTransactionTest() {
        User user = new User("user1");
        user.setMedianTransactionAmount(200.0);
        when(userDAO.getUser("user1")).thenReturn(user);
        {
            Transaction transaction = new Transaction("user1", 100.0, LocalDateTime.of(2024, 3, 14, 3, 0), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            LOGGER.info(String.format(fraudFlaggers.toString()));
            assertTrue(fraudFlaggers.contains(FraudFlagger.ODD_TIME_TRANSACTION));
        }
        {
            Transaction transaction = new Transaction("user1", 100.0, LocalDateTime.of(2024, 3, 14, 5, 0), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            LOGGER.info(String.format(fraudFlaggers.toString()));
            assertTrue(fraudFlaggers.contains(FraudFlagger.ODD_TIME_TRANSACTION));
        }
        {
            Transaction transaction = new Transaction("user1", 100.0, LocalDateTime.of(2024, 3, 14, 12, 0), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            assertFalse(fraudFlaggers.contains(FraudFlagger.ODD_TIME_TRANSACTION));
        }
        {
            Transaction transaction = new Transaction("user1", 100.0, LocalDateTime.of(2024, 3, 14, 17, 0), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            assertFalse(fraudFlaggers.contains(FraudFlagger.ODD_TIME_TRANSACTION));
        }
    }

    /**
     * This test checks if the FraudDetectionService correctly identifies frequent transactions as fraudulent.
     */
    @Test
    public void tooManyTransactionAcrossMerchantTest() {
        User user = new User("user1");
        user.setMedianTransactionAmount(100.0);
        when(userDAO.getUser("user1")).thenReturn(user);
        {
            List<Transaction> previousTransactions = new ArrayList<>(
                    Arrays.asList(
                            new Transaction("user1", 100.0, LocalDateTime.now().minusSeconds(1), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusSeconds(2), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusSeconds(3), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusSeconds(4), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusSeconds(5), "Store A")
                    )
            );
            when(transactionDAO.getPreviousTransactionsForAUser(any(), eq("user1"))).thenReturn(previousTransactions);
            Transaction transaction = new Transaction("user1", 100.0, LocalDateTime.now(), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            LOGGER.info(String.format(fraudFlaggers.toString()));
            assertTrue(fraudFlaggers.contains(FraudFlagger.TOO_MANY_TRANSACTION_ACROSS_MERCHANT));
        }
        {
            List<Transaction> previousTransactions = new ArrayList<>(
                    Arrays.asList(
                            new Transaction("user1", 100.0, LocalDateTime.now().minusSeconds(1), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusSeconds(2), "Store A")
                    )
            );
            when(transactionDAO.getPreviousTransactionsForAUser(any(), eq("user1"))).thenReturn(previousTransactions);
            Transaction transaction = new Transaction("user1", 100.0, LocalDateTime.now(), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            assertFalse(fraudFlaggers.contains(FraudFlagger.TOO_MANY_TRANSACTION_ACROSS_MERCHANT));
        }
        {
            List<Transaction> previousTransactions = new ArrayList<>(
                    Arrays.asList(
                            new Transaction("user1", 100.0, LocalDateTime.now().minusMinutes(1), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusMinutes(2), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusMinutes(3), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusMinutes(4), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusMinutes(5), "Store A")
                    )
            );
            when(transactionDAO.getPreviousTransactionsForAUser(any(), eq("user1"))).thenReturn(previousTransactions);
            Transaction transaction = new Transaction("user1", 100.0, LocalDateTime.now(), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            LOGGER.info(String.format(fraudFlaggers.toString()));
            assertTrue(fraudFlaggers.contains(FraudFlagger.TOO_MANY_TRANSACTION_ACROSS_MERCHANT));
        }
        {
            List<Transaction> previousTransactions = new ArrayList<>(
                    Arrays.asList(
                            new Transaction("user1", 100.0, LocalDateTime.now().minusMinutes(1), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusMinutes(2), "Store A")
                    )
            );
            when(transactionDAO.getPreviousTransactionsForAUser(any(), eq("user1"))).thenReturn(previousTransactions);
            Transaction transaction = new Transaction("user1", 100.0, LocalDateTime.now(), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            assertFalse(fraudFlaggers.contains(FraudFlagger.TOO_MANY_TRANSACTION_ACROSS_MERCHANT));
        }

    }

    /**
     * This test checks if the FraudDetectionService correctly identifies transactions made with fraudulent merchants as fraudulent.
     */
    @Test
    public void fraudulentMerchantTest() {
        User user = new User("user1");
        user.setMedianTransactionAmount(100.0);
        when(userDAO.getUser("user1")).thenReturn(user);

        {
            when(merchantDAO.getFraudulentCount("Store A")).thenReturn(12);
            Transaction transaction = new Transaction("user1", 100.0, LocalDateTime.now(), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            LOGGER.info(String.format(fraudFlaggers.toString()));
            assertTrue(fraudFlaggers.contains(FraudFlagger.FRAUDULENT_MERCHANT));
        }
        {
            when(merchantDAO.getFraudulentCount("Store A")).thenReturn(1);
            Transaction transaction = new Transaction("user1", 100.0, LocalDateTime.now(), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            assertFalse(fraudFlaggers.contains(FraudFlagger.FRAUDULENT_MERCHANT));
        }
    }

    /**
     * This test checks if the FraudDetectionService correctly identifies inconsistent transactions as fraudulent.
     */
    @Test
    public void tooManyTransactionWithSameMerchantTest() {
        User user = new User("user1");
        user.setMedianTransactionAmount(100.0);
        when(userDAO.getUser("user1")).thenReturn(user);

        {
            List<Transaction> previousTransactions = new ArrayList<>(
                    Arrays.asList(
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(1), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(2), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(2), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(4), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(5), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(6), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(7), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(8), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(9), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(10), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(11), "Store A")
                    )
            );
            when(transactionDAO.getPreviousTransactionsForAUser(any(), eq("user1"))).thenReturn(previousTransactions);
            Transaction transaction = new Transaction("user1", 100.0, LocalDateTime.now(), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            LOGGER.info(String.format(fraudFlaggers.toString()));
            assertTrue(fraudFlaggers.contains(FraudFlagger.TOO_MANY_TRANSACTION_WITH_SAME_MERCHANT));
        }

        {
            List<Transaction> previousTransactions = new ArrayList<>(
                    Arrays.asList(
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(1), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(2), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(2), "Store A"),
                            new Transaction("user1", 100.0, LocalDateTime.now().minusHours(4), "Store A")
                    )
            );
            when(transactionDAO.getPreviousTransactionsForAUser(any(), eq("user1"))).thenReturn(previousTransactions);
            Transaction transaction = new Transaction("user1", 100.0, LocalDateTime.now(), "Store A");
            List<FraudFlagger> fraudFlaggers = fraudDetectionService.detectFraud(transaction);
            assertFalse(fraudFlaggers.contains(FraudFlagger.TOO_MANY_TRANSACTION_WITH_SAME_MERCHANT));
        }
    }
}