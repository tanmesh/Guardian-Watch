package org.example.service;

import org.example.entity.Transaction;

import java.io.IOException;

/**
 * This service class is responsible for reading transactions from a CSV file.
 * It uses a BufferedReader to read the file line by line.
 * Each line is parsed into a Transaction object and added to the corresponding User and Merchant.
 */
public interface ITransactionReaderService {
    /**
     * Reads the next line from the CSV file and parses it into a Transaction object.
     * It also updates the User and Merchant with the new transaction.
     *
     * @return The next Transaction object, or null if there are no more transactions.
     * @throws IOException If an I/O error occurs.
     */
    Transaction getNextTransaction() throws IOException;
}
