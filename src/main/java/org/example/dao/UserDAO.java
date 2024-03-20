package org.example.dao;

import lombok.Getter;
import lombok.Setter;
import org.example.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserDAO {
    private Map<String, User> userMap = new HashMap<>();

    /**
     * This method retrieves a user from the user map based on the user ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The User object corresponding to the given user ID, or null if no such user exists.
     */
    public User getUser(String userId) {
        return userMap.get(userId);
    }

    /**
     * This method adds a new user to the user map.
     * If a user with the same ID already exists in the map, it will be replaced.
     *
     * @param user The User object to be added.
     */
    public void addUser(User user) {
        userMap.put(user.getUserId(), user);
    }

    /**
     * This method retrieves a list of all users in the system.
     *
     * @return A list of all User objects in the system.
     */
    public List<User> getUserList() {
        return new ArrayList<>(userMap.values());
    }

    /**
     * This method sets the median transaction amount for a user.
     *
     * @param userId                  The ID of the user to update.
     * @param medianTransactionAmount The new median transaction amount for the user.
     */
    public void setMedianTransactionAmount(String userId, Double medianTransactionAmount) {
        userMap.get(userId).setMedianTransactionAmount(medianTransactionAmount);
    }
}