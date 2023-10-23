# Bank Account Micro Web Service

This project implements a simple micro web service mimicking a "Bank Account" using REST API design principles. The service allows users to create an account, inquire about their balance, deposit money, and withdraw money. It is developed using Java, Spring Boot, Spring Framework, and H2 database.

## Features

The web service includes the following features:

- Three REST API endpoints: Create, Balance, Deposit, and Withdrawal
- No authentication required
- H2 database for data storage
- Error handling for various scenarios with appropriate HTTP status codes and error messages

## Endpoints

The service provides the following endpoints:

### 1. Account Creation

- **Endpoint:** `/api/user`
- **Description:** Creates a user account and returns account details
- **Example API Call:**
```http POST /api/user```
  {
  "firstName": "Eugene",
  "lastName": "Odhiambo",
  "otherName": "Odongo",
  "gender": "MALE",
  "address": "Marcus Garvey Rd, Kilimani - Nairobi",
  "countryOfOrigin": "KENYA",
  "email": "euginedevv@gmail.com",
  "phoneNumber": "+254712345678",
  "alternativePhoneNumber": "+254712345678"
}


### 2. Balance

- **Endpoint:** `/api/user/balance`
- **Description:** Returns the outstanding account balance.
- - **Example API Call:**
    ```http GET /api/user/balance```
    {
    "accountNumber": "2023478828"
    }

### 2. Deposit

- **Endpoint:** `/api/user/credit`
- **Description:** Credits the account with the specified amount.
- **Limits:**
    - Maximum deposit for the day: $150,000
    - Maximum deposit per transaction: $40,000
    - Maximum deposit frequency: 4 transactions per day
- **Example API Call:**
  ```http POST /api/user/credit```
  {
  "accountNumber": "2023584863",
  "amount": 4000
}

### 3. Withdrawal

- **Endpoint:** `/api/user/debit`
- **Description:** Deducts the account with the specified amount.
- **Limits:**
    - Maximum withdrawal for the day: $50,000
    - Maximum withdrawal per transaction: $20,000
    - Maximum withdrawal frequency: 3 transactions per day
    - Cannot withdraw when the balance is less than the withdrawal amount
- **Example API Call:**
  ```http POST /api/user/debit```
  {
  "accountNumber": "2023584863",
  "amount": 4000
  }


## Implementation Details

- The project is implemented using Java and the Spring Boot framework, which makes it easy to create RESTful web services.
- An H2 in-memory database is used to store account information and transaction data.
- Extensive error handling is implemented to return appropriate error codes and messages for various scenarios.

## Testing

- The project includes unit tests for the web service, covering different scenarios and edge cases.
- You can use a code coverage tool to measure code coverage and ensure comprehensive testing.

## Running the Service

To run the service, follow these steps:

1. Clone the repository from Github.
2. Ensure you have Java 17 and Maven installed on your system.
3. Build the project using Maven: `mvn clean install`
4. Run the application: `mvn spring-boot:run`

The service will start, and you can access the endpoints using a tool like Postman or by making HTTP requests.

## Additional Notes

This is a simple yet effective example of a micro web service for managing a bank account. The implementation adheres to REST API principles, includes appropriate limits and error handling, and provides a database for data storage.

Feel free to explore the code and run the service to understand its functionality better.
