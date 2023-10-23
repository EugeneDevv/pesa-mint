package com.eugenedevv.pesamint.service.impl;

import com.eugenedevv.pesamint.dto.BankResponse;
import com.eugenedevv.pesamint.dto.CreditDebitRequest;
import com.eugenedevv.pesamint.dto.EnquiryRequest;
import com.eugenedevv.pesamint.dto.UserRequest;
import com.eugenedevv.pesamint.entity.Transaction;
import com.eugenedevv.pesamint.entity.TransactionType;
import com.eugenedevv.pesamint.entity.User;
import com.eugenedevv.pesamint.repository.TransactionRepository;
import com.eugenedevv.pesamint.repository.UserRepository;
import com.eugenedevv.pesamint.utils.AccountUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by Eugene Devv on 23 Oct, 2023
 */

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    User user;
    UserRequest request;
    EnquiryRequest enquiryRequest;
    CreditDebitRequest creditDebitRequest;
    List<Transaction> transactionList;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Eugene", "Odhiambo", "Odongo", "MALE",
                "Test Address", "KE", "12345678", new BigDecimal("1000.25"),
                "eugene@example.com", "+254712345678", "+254787654321", null,
                null, null, null);

        request = UserRequest.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .otherName(user.getOtherName())
                .gender(user.getGender())
                .address(user.getAddress())
                .countryOfOrigin(user.getCountryOfOrigin())
                .phoneNumber(user.getPhoneNumber())
                .alternativePhoneNumber(user.getAlternativePhoneNumber())
                .build();

        enquiryRequest = EnquiryRequest.builder()
                .accountNumber("12345678")
                .build();

        creditDebitRequest = CreditDebitRequest.builder()
                .accountNumber("12345678")
                .amount(new BigDecimal("20.00"))
                .build();

        transactionList = new ArrayList<>();
        transactionList.add(Transaction.builder()
                .transactionDate(new Date())
                .amount(200.00)
                .transactionType(TransactionType.CREDIT).user(user)
                .build());


        transactionList.add(Transaction.builder()
                .transactionDate(new Date())
                .amount(600.00)
                .transactionType(TransactionType.CREDIT).user(user)
                .build());

    }

    @AfterEach
    void tearDown() {
        user = null;
        request = null;
        enquiryRequest = null;
        creditDebitRequest = null;
        userRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    @Test
    public void testCreateAccount_ReturnsBankResponse() {

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        BankResponse savedUser = userService.createAccount(request);
        assertThat(savedUser.getAccountInfo().getAccountName())
                .isEqualTo(request.getFirstName() + " " + request.getLastName() + " " + request.getOtherName());
    }

    @Test
    public void testCreateAccount_existsByEmail() {

        when(userRepository.existsByEmail("eugene@example.com")).thenReturn(true);

        BankResponse savedUser = userService.createAccount(request);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.ACCOUNT_EXISTS_WITH_EMAIL_MESSAGE);
    }

    @Test
    public void testCreateAccount_existsByPhoneNumber() {

        when(userRepository.existsByEmail(Mockito.any(String.class))).thenReturn(false);
        when(userRepository.existsByPhoneNumber(Mockito.any(String.class))).thenReturn(true);


        BankResponse savedUser = userService.createAccount(request);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.ACCOUNT_EXISTS_WITH_PHONE_MESSAGE);
    }


    @Test
    public void testCreateAccount_existsByAlternativePhoneNumber() {

        when(userRepository.existsByEmail(Mockito.any(String.class))).thenReturn(false);
        when(userRepository.existsByPhoneNumber(Mockito.any(String.class))).thenReturn(false);
        when(userRepository.existsByAlternativePhoneNumber("+254787654321")).thenReturn(true);

        BankResponse savedUser = userService.createAccount(request);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.ACCOUNT_EXISTS_WITH_PHONE_MESSAGE);
    }

    @Test
    public void testCreateAccount_existsByAccountNumber() {

        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);

        BankResponse savedUser = userService.createAccount(request);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.ACCOUNT_EXISTS_MESSAGE);
    }

    @Test
    public void testDetailsEnquiry_ReturnsBankResponse() {

        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);
        when(userRepository.findByAccountNumber(Mockito.any(String.class))).thenReturn(user);

        BankResponse savedUser = userService.detailsEnquiry(enquiryRequest);
        assertThat(savedUser.getAccountInfo().getAccountName())
                .isEqualTo(request.getFirstName() + " " + request.getLastName() + " " + request.getOtherName());
    }

    @Test
    public void testDetailsEnquiry_accountExistsFalse() {

        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(false);

        BankResponse savedUser = userService.detailsEnquiry(enquiryRequest);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE);
    }

    @Test
    public void testCreditAccount_accountExistsFalse() {

        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(false);

        BankResponse savedUser = userService.creditAccount(creditDebitRequest);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE);
    }

    @Test
    public void testCreditAccount_maxCreditAmountTrue() {

        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);
        creditDebitRequest.setAmount(new BigDecimal("40001.0"));
        BankResponse savedUser = userService.creditAccount(creditDebitRequest);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.EXCEEDED_MAX_DEPOSIT_MESSAGE);
    }

    @Test
    public void testCreditAccount_maxCreditFrequencyTrue() {

        transactionList.add(Transaction.builder()
                .transactionDate(new Date())
                .amount(400.00)
                .transactionType(TransactionType.CREDIT).user(user)
                .build());


        transactionList.add(Transaction.builder()
                .transactionDate(new Date())
                .amount(300.00)
                .transactionType(TransactionType.CREDIT).user(user)
                .build());

        when(userRepository.findByAccountNumber(Mockito.any(String.class))).thenReturn(user);
        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);
        when(transactionRepository.findTransactionsByUserAndTransactionDateAndTransactionType(Mockito.any(User.class),
                Mockito.any(Date.class),Mockito.any(TransactionType.class))).thenReturn(transactionList);

        BankResponse savedUser = userService.creditAccount(creditDebitRequest);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.EXCEEDED_MAX_DEPOSIT_FREQUENCY_MESSAGE);
    }


    @Test
    public void testCreditAccount_ReturnsBankResponse() {

        transactionList.add(Transaction.builder()
                .transactionDate(new Date())
                .amount(300.00)
                .transactionType(TransactionType.CREDIT).user(user)
                .build());

        when(userRepository.findByAccountNumber(Mockito.any(String.class))).thenReturn(user);
        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);
        when(transactionRepository.findTransactionsByUserAndTransactionDateAndTransactionType(Mockito.any(User.class),
                Mockito.any(Date.class),Mockito.any(TransactionType.class))).thenReturn(transactionList);

        BankResponse savedUser = userService.creditAccount(creditDebitRequest);
        assertThat(savedUser.getAccountInfo().getAccountName())
                .isEqualTo(request.getFirstName() + " " + request.getLastName() + " " + request.getOtherName());
    }
    @Test
    public void testCreditAccount_maxCreditPerDayTrue() {

        transactionList.add(Transaction.builder()
                .transactionDate(new Date())
                .amount(149300.00)
                .transactionType(TransactionType.CREDIT).user(user)
                .build());

        when(userRepository.findByAccountNumber(Mockito.any(String.class))).thenReturn(user);
        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);
        when(transactionRepository.findTransactionsByUserAndTransactionDateAndTransactionType(Mockito.any(User.class),
                Mockito.any(Date.class),Mockito.any(TransactionType.class))).thenReturn(transactionList);

        BankResponse savedUser = userService.creditAccount(creditDebitRequest);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.generateMaxDepositErrorMessage(150100.00));
    }

    @Test
    public void testDebitAccount_accountExistsFalse() {

        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(false);

        BankResponse savedUser = userService.debitAccount(creditDebitRequest);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE);
    }

    @Test
    public void testDebitAccount_maxDebitAmountTrue() {

        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);
        creditDebitRequest.setAmount(new BigDecimal("40001.0"));
        BankResponse savedUser = userService.debitAccount(creditDebitRequest);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.EXCEEDED_MAX_WITHDRAWAL_MESSAGE);
    }

    @Test
    public void tesDebitAccount_balanceAvailableTrue() {

        creditDebitRequest.setAmount(new BigDecimal("1900.00"));


        when(userRepository.findByAccountNumber(Mockito.any(String.class))).thenReturn(user);
        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);

        BankResponse savedUser = userService.debitAccount(creditDebitRequest);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.PAYMENT_REQUIRED);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE);
    }


    @Test
    public void testDebitAccount_maxDebitFrequencyTrue() {

        transactionList.add(Transaction.builder()
                .transactionDate(new Date())
                .amount(400.00)
                .transactionType(TransactionType.CREDIT).user(user)
                .build());


        when(userRepository.findByAccountNumber(Mockito.any(String.class))).thenReturn(user);
        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);
        when(transactionRepository.findTransactionsByUserAndTransactionDateAndTransactionType(Mockito.any(User.class),
                Mockito.any(Date.class),Mockito.any(TransactionType.class))).thenReturn(transactionList);

        BankResponse savedUser = userService.debitAccount(creditDebitRequest);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.EXCEEDED_MAX_WITHDRAWAL_FREQUENCY_MESSAGE);
    }


    @Test
    public void tesDebitAccount_maxDebitPerDayTrue() {

        transactionList.get(0).setAmount(20000.00);
        transactionList.get(1).setAmount(20000.00);

        creditDebitRequest.setAmount(new BigDecimal("20000.00"));

        user.setAccountBalance(new BigDecimal("100000.0"));

        when(userRepository.findByAccountNumber(Mockito.any(String.class))).thenReturn(user);
        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);
        when(transactionRepository.findTransactionsByUserAndTransactionDateAndTransactionType(Mockito.any(User.class),
                Mockito.any(Date.class),Mockito.any(TransactionType.class))).thenReturn(transactionList);

        BankResponse savedUser = userService.debitAccount(creditDebitRequest);
        assertThat(savedUser.getHttpStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(savedUser.getResponseMessage())
                .isEqualTo(AccountUtils.generateMaxWithdrawalErrorMessage(40000.00));
    }


//
//    @Test
//    public void testCreditAccount_ReturnsBankResponse() {
//
//        transactionList.add(Transaction.builder()
//                .transactionDate(new Date())
//                .amount(300.00)
//                .transactionType(TransactionType.CREDIT).user(user)
//                .build());
//
//        when(userRepository.findByAccountNumber(Mockito.any(String.class))).thenReturn(user);
//        when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);
//        when(transactionRepository.findTransactionsByUserAndTransactionDateAndTransactionType(Mockito.any(User.class),
//                Mockito.any(Date.class),Mockito.any(TransactionType.class))).thenReturn(transactionList);
//
//        BankResponse savedUser = userService.creditAccount(creditDebitRequest);
//        assertThat(savedUser.getAccountInfo().getAccountName())
//                .isEqualTo(request.getFirstName() + " " + request.getLastName() + " " + request.getOtherName());
//    }


}