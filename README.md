# Guardian Watch - Secure Transaction Monitoring System

## Overview
Guardian Watch is a secure transaction monitoring system designed to detect fraudulent transactions based on various criteria. The system is implemented in Java using Maven and follows a modular architecture for easy maintenance and scalability.

## Features
- **High-Amount Transactions**: The system flags transactions that exceed a predefined limit. This helps in identifying potential fraudulent activities involving large sums of money.
- **Odd-Time Transactions**: Transactions made during unusual hours or time intervals are flagged. This feature helps in identifying potential fraudulent activities that are often carried out during off-peak hours to avoid detection.
- **Frequent Transactions Across Multiple Merchants**: The system flags users who perform frequent transactions across multiple merchants within a specified timeframe. 
- **Repetitive Transactions with Same Merchant**: The system flags users who perform repetitive transactions with the same merchant. This could indicate potential fraudulent activities such as money laundering or misuse of credit card information.
- **Transactions Involving Fraudulent Merchants**: The system identifies transactions involving known fraudulent merchants or entities.

## Architecture
The system follows a modular architecture with the following components:

1. **Core Service**
- `FraudDetectionService`: This is the main service class responsible for encapsulating the fraud detection logic and coordinating the overall process. It orchestrates the interactions between various components and performs the necessary computations to determine potential fraudulent activities.

2. **Data Access Objects**
- `UserDAO`, `MerchantDAO`, `TransactionDAO`: These Data Access Object (DAO) classes are responsible for interacting with the database and retrieving relevant data required for fraud detection. Each DAO class is dedicated to handling data related to users, merchants, and transactions, respectively.

3. **Entities**
- `User`, `Transaction`, `Merchant`: These classes represent the data model of the system. They encapsulate the properties and behavior of user, transaction, and merchant entities, respectively. 

## Design Principles
The Guardian Watch system follows several design principles to ensure maintainability, scalability, and extensibility:

- **Separation of Concerns**: The system is modularized into separate components, each responsible for a specific concern (e.g., fraud detection logic, data access, entity modeling).
- **Single Responsibility Principle**: Each class has a single responsibility, promoting code reusability and easier maintenance.
- **Open/Closed Principle**: The fraud detection rules are designed to be open for extension but closed for modification, allowing for the addition of new rules without modifying the existing ones.

## Extension Plans
The Guardian Watch system is designed with extensibility in mind, allowing for future enhancements and additions:

- **Integration with Database**: Integrating the existing system with a database like Mysql/Postgres to store and retrieve transaction, user, and merchant data. This will add durability and persistence to the system.
- **More robust User Pattern detection algorithm**: Implementing a more robust algorithm to detect user patterns to identify unusual transaction behavior and detect fradulent transactions. Ex, learning user expenditure patterns for a given user and leveraging that to flag transactions.
- **Concurrency and thread safety**: We can use multi-threading and concurrency to improve the throughput of the system. Specifically, we can process multiple transactions at the same time by multiple threads. We will have to refactor the code appropriately to make it thread-safe.

## Conclusion
The Guardian Watch system provides a robust and extensible solution for secure transaction monitoring and fraud detection. By following modular design principles and implementing configurable fraud detection rules, the system ensures maintainability and adaptability to evolving fraud patterns. With the proposed extension plans, the system can further enhance its capabilities and provide a comprehensive solution for secure financial transactions.