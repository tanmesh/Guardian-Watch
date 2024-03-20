package org.example.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Merchant {
    private UUID id;
    private String name;
    private int fraudulentCount;

    public Merchant() {
    }

    public Merchant(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
        this.fraudulentCount = 0;
    }

    public void setFraudulent() {
        ++fraudulentCount;
    }
}