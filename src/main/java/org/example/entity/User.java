package org.example.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;


@Getter
@Setter
public class User {
    private String userId;
    private String emailId;
    private String firstName;
    private String lastName;
    private Double medianTransactionAmount;

    public User(String userId) {
        this.userId = userId;
        this.medianTransactionAmount = 0.0;
    }
}