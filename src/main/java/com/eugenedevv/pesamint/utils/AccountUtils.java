package com.eugenedevv.pesamint.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "409";
    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "201";
    public static final String ACCOUNT_EXISTS_WITH_EMAIL_MESSAGE = "An account with this email already exists!";
    public static final String ACCOUNT_EXISTS_WITH_PHONE_MESSAGE = "An account with this phone number already exists!";
    public static final String ACCOUNT_EXISTS_MESSAGE = "Account already exists, please try again";
    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Account has been successfully created!";

    public  static String generateAccountNumber(){

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
}
