package com.eugenedevv.pesamint.service.impl;

import com.eugenedevv.pesamint.dto.*;
import com.eugenedevv.pesamint.entity.Transaction;
import com.eugenedevv.pesamint.entity.TransactionType;
import com.eugenedevv.pesamint.entity.User;
import com.eugenedevv.pesamint.repository.TransactionRepository;
import com.eugenedevv.pesamint.repository.UserRepository;
import com.eugenedevv.pesamint.service.EmailService;
import com.eugenedevv.pesamint.service.UserService;
import com.eugenedevv.pesamint.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        /*
         * Check if user already has an account
         * Creating an account - saving a new user into the dd
         */

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.CONFLICT.value())
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_WITH_EMAIL_MESSAGE)
                    .accountInfo(null)
                    .httpStatus(HttpStatus.CONFLICT)
                    .build();
        }

        if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.CONFLICT.value())
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_WITH_PHONE_MESSAGE)
                    .accountInfo(null).httpStatus(HttpStatus.CONFLICT)
                    .build();
        }
        if (userRepository.existsByAlternativePhoneNumber(userRequest.getAlternativePhoneNumber())) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.CONFLICT.value())
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_WITH_PHONE_MESSAGE)
                    .accountInfo(null).httpStatus(HttpStatus.CONFLICT)
                    .build();
        }

        final String accountNumber = AccountUtils.generateAccountNumber();

        if (userRepository.existsByAccountNumber(accountNumber)) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.CONFLICT.value())
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null).httpStatus(HttpStatus.CONFLICT)
                    .build();
        }


        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .countryOfOrigin(userRequest.getCountryOfOrigin())
                .accountNumber(accountNumber)
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("PENDING")
                .build();

        User savedUser = userRepository.save(newUser);

//        Send Email Alert

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account Has Been Created Successfully.\nYour Account Details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() +
                        "\nAccount Number: " + savedUser.getAccountNumber())
                .build();

        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(HttpStatus.CREATED.value())
                .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE).httpStatus(HttpStatus.CREATED)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public BankResponse detailsEnquiry(EnquiryRequest request) {
//        Check if the provided account number exists in the db
        boolean accountExists = (userRepository.existsByAccountNumber(request.getAccountNumber()));
        if (!accountExists) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.NOT_FOUND.value())
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(HttpStatus.OK.value())
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //        Check if the provided account number exists in the db
        boolean accountExists = (userRepository.existsByAccountNumber(request.getAccountNumber()));
        if (!accountExists) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.NOT_FOUND.value())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        double creditAmount = Double.parseDouble(request.getAmount().toString());
        if (creditAmount > AccountUtils.MAX_DEPOSIT_PER_TRANSACTION) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.FORBIDDEN.value())
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .responseMessage(AccountUtils.EXCEEDED_MAX_DEPOSIT_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());

        List<Transaction> transactionList = transactionRepository.findTransactionsByUserAndTransactionDateAndTransactionType(userToCredit, AccountUtils.getCurrentDate(), TransactionType.CREDIT);
        if (transactionList.size() >= AccountUtils.MAX_DEPOSIT_FREQUENCY_PER_DAY) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.FORBIDDEN.value())
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .responseMessage(AccountUtils.EXCEEDED_MAX_DEPOSIT_FREQUENCY_MESSAGE)
                    .accountInfo(null)
                    .build();

        }

        double totalTransactions = 0;

        for (Transaction value : transactionList) {
            totalTransactions += value.getAmount();
        }

        if ((totalTransactions + creditAmount) > AccountUtils.MAX_DEPOSIT_PER_DAY) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.FORBIDDEN.value())
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .responseMessage(AccountUtils.generateMaxDepositErrorMessage(totalTransactions))
                    .accountInfo(null)
                    .build();
        }

        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));

        Transaction transaction = Transaction.builder()
                .user(userToCredit)
                .transactionType(TransactionType.CREDIT)
                .amount(Double.parseDouble(request.getAmount().toString()))
                .transactionDate(AccountUtils.getCurrentDate())
                .build();
        transactionRepository.save(transaction);

        userRepository.save(userToCredit);

        return BankResponse.builder()
                .responseCode(HttpStatus.OK.value())
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //        Check if the provided account number exists in the db
        boolean accountExists = (userRepository.existsByAccountNumber(request.getAccountNumber()));
        if (!accountExists) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.NOT_FOUND.value())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();

        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());

        //        Check if the amount the user intends to withdraw is not more than the available balance
        double availableBalance = Double.parseDouble(userToDebit.getAccountBalance().toString());
        double debitAmount = Double.parseDouble(request.getAmount().toString());
        if (debitAmount > AccountUtils.MAX_WITHDRAWAL_PER_TRANSACTION) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.FORBIDDEN.value())
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .responseMessage(AccountUtils.EXCEEDED_MAX_WITHDRAWAL_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else if (availableBalance < debitAmount) { // Check if there is enough amount to debit
            return BankResponse.builder()
                    .responseCode(HttpStatus.PAYMENT_REQUIRED.value())
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .httpStatus(HttpStatus.PAYMENT_REQUIRED)
                    .accountInfo(null)
                    .build();
        }
        List<Transaction> transactionList = transactionRepository.findTransactionsByUserAndTransactionDateAndTransactionType(userToDebit, AccountUtils.getCurrentDate(), TransactionType.DEBIT);
        if (transactionList.size() >= AccountUtils.MAX_WITHDRAWAL_FREQUENCY_PER_DAY) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.FORBIDDEN.value())
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .responseMessage(AccountUtils.EXCEEDED_MAX_WITHDRAWAL_FREQUENCY_MESSAGE)
                    .accountInfo(null)
                    .build();

        }

        double totalTransactions = 0;

        for (Transaction value : transactionList) {
            totalTransactions += value.getAmount();
        }

        if ((totalTransactions + debitAmount) > AccountUtils.MAX_WITHDRAWAL_PER_DAY) {
            return BankResponse.builder()
                    .responseCode(HttpStatus.FORBIDDEN.value())
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .responseMessage(AccountUtils.generateMaxWithdrawalErrorMessage(totalTransactions))
                    .accountInfo(null)
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            Transaction transaction = Transaction.builder()
                    .user(userToDebit)
                    .transactionType(TransactionType.DEBIT)
                    .amount(debitAmount)
                    .transactionDate(AccountUtils.getCurrentDate())
                    .build();
            transactionRepository.save(transaction);

            userRepository.save(userToDebit);

            return BankResponse.builder()
                    .responseCode(HttpStatus.OK.value())
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                    .httpStatus(HttpStatus.OK)
                    .accountInfo(AccountInfo.builder()
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountNumber(request.getAccountNumber())
                            .build())
                    .build();
        }

    }
}
