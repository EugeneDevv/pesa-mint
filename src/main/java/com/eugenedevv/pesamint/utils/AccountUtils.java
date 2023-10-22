package com.eugenedevv.pesamint.utils;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class AccountUtils {

    public static final double MAX_DEPOSIT_PER_DAY = 150000;
    public static final double MAX_DEPOSIT_PER_TRANSACTION = 40000;
    public static final double MAX_DEPOSIT_FREQUENCY_PER_DAY = 4;
    public static final double MAX_WITHDRAWAL_PER_DAY = 50000;
    public static final double MAX_WITHDRAWAL_PER_TRANSACTION = 20000;
    public static final double MAX_WITHDRAWAL_FREQUENCY_PER_DAY = 3;

    //    Error codes
    public static final String RATE_LIMIT_CODE = "429"; // Used for: credit/debit limit reached


    //    Error messages
    public static final String ACCOUNT_EXISTS_WITH_EMAIL_MESSAGE = "An account with this email already exists!";
    public static final String ACCOUNT_EXISTS_WITH_PHONE_MESSAGE = "An account with this phone number already exists!";
    public static final String ACCOUNT_EXISTS_MESSAGE = "Account already exists, please try again";
    public static final String ACCOUNT_NOT_EXISTS_MESSAGE = "User with the provided Account Number does not exist";
    public static final String EXCEEDED_MAX_DEPOSIT_MESSAGE = "You Exceeded Maximum Deposit Per Transaction";
    public static final String EXCEEDED_MAX_WITHDRAWAL_MESSAGE = "You Exceeded Maximum Withdrawal Per Transaction";
    public static final String EXCEEDED_MAX_WITHDRAWAL_FREQUENCY_MESSAGE = "You exceeded the maximum number of withdrawals for today!";
    public static final String EXCEEDED_MAX_WITHDRAWAL_FOR_TODAY_MESSAGE = "You exceeded the maximum amount you can withdraw for today!";
    public static final String EXCEEDED_MAX_DEPOSIT_FOR_TODAY_MESSAGE = "You exceeded the maximum amount you can deposit for today!";
    public static final String EXCEEDED_MAX_DEPOSIT_FREQUENCY_MESSAGE = "You exceeded the maximum number of deposits for today!";

    public static String generateMaxWithdrawalErrorMessage(Double totalTransactions) {

        final double balance = MAX_WITHDRAWAL_PER_DAY - totalTransactions;

        if(balance > 0){
            return EXCEEDED_MAX_WITHDRAWAL_FOR_TODAY_MESSAGE + " You can only withdraw up to $"+balance;
        }
        return EXCEEDED_MAX_WITHDRAWAL_FOR_TODAY_MESSAGE;
    }
    public static String generateMaxDepositErrorMessage(Double totalTransactions) {

        final double balance = MAX_DEPOSIT_PER_DAY - totalTransactions;

        if(balance > 0){
            return EXCEEDED_MAX_DEPOSIT_FOR_TODAY_MESSAGE + " You can only deposit up to $"+balance;
        }
        return EXCEEDED_MAX_DEPOSIT_FOR_TODAY_MESSAGE;
    }

    //    Success codes
    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "201";

    //    Success Messages
    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Account has been successfully created!";
    public static final String ACCOUNT_FOUND_SUCCESS_MESSAGE = "User Account Found";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User Account Credited Successfully";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "You have insufficient balance";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Withdrawal successful";

    public static String generateAccountNumber() {

        /*
         *   2023 + randomSixDigits
         */
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        // generate a random number between min and max
        int randNumber = (int) (Math.random() * (max - min + 1) + min);

        // convert the currentYear and randomNumber to strings, then concatenate them

        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);

        return year + randomNumber;

    }

    public static Date getCurrentDate(){
        // Get the current LocalDateTime
//        LocalDateTime localDateTime = LocalDateTime.now();
//
//        // Convert LocalDateTime to Date
//        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
//        System.out.println(date);

        // Create a Date object
        Date date = new Date();

        // Create a Calendar instance and set it to the given Date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Set hour, minute, second, and millisecond components to zero
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Get the updated Date without time components
        return calendar.getTime();
    }
}
