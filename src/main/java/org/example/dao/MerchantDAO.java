package org.example.dao;

import lombok.Getter;
import lombok.Setter;
import org.example.entity.Merchant;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a Data Access Object (DAO) for Merchant.
 * It provides methods to interact with the merchant data.
 */
@Getter
@Setter
public class MerchantDAO {
    private Set<Merchant> merchantList = new HashSet<>();

    /**
     * This method adds a new merchant to the merchant list.
     * If the merchant already exists in the list, it does not add it again.
     *
     * @param merchant The merchant to be added.
     */
    public void addMerchant(Merchant merchant) {
        if (merchantExists(merchant.getName())) {
            return;
        }
        merchantList.add(merchant);
    }

    /**
     * This method checks if a merchant with the given name exists in the merchant list.
     *
     * @param merchantName The name of the merchant to check.
     * @return true if the merchant exists, false otherwise.
     */
    public boolean merchantExists(String merchantName) {
        for (Merchant merchant : merchantList) {
            if (merchant.getName().equals(merchantName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks if a merchant is marked as fraudulent.
     *
     * @param merchantName The name of the merchant to check.
     * @return true if the merchant is marked as fraudulent, false otherwise.
     */
    public int getFraudulentCount(String merchantName) {
        for (Merchant merchant : merchantList) {
            if (merchant.getName().equals(merchantName)) {
                return merchant.getFraudulentCount();
            }
        }
        return 0;
    }

    /**
     * This method sets a merchant's fraudulent status.
     *
     * @param merchantName The name of the merchant to update.
     */
    public void setFraudulentCount(String merchantName) {
        for (Merchant merchant : merchantList) {
            if (merchant.getName().equals(merchantName)) {
                merchant.setFraudulent();
                return;
            }
        }
    }
}