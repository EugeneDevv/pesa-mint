package com.eugenedevv.pesamint.service.impl;

import com.eugenedevv.pesamint.dto.AccountInfo;
import com.eugenedevv.pesamint.dto.BankResponse;
import com.eugenedevv.pesamint.dto.UserRequest;
import com.eugenedevv.pesamint.entity.User;
import com.eugenedevv.pesamint.repository.UserRepository;
import com.eugenedevv.pesamint.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        /*
         * Check if user already has an account
         * Creating an account - saving a new user into the dd
         */

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_WITH_EMAIL_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_WITH_PHONE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        if (userRepository.existsByAlternativePhoneNumber(userRequest.getAlternativePhoneNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_WITH_PHONE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        final String accountNumber = AccountUtils.generateAccountNumber();

        if (userRepository.existsByAccountNumber(accountNumber)) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
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

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName()+ " " + savedUser.getOtherName())
                        .build())
                .build();
    }
}
