package com.eugenedevv.pesamint.repository;

import com.eugenedevv.pesamint.entity.Transaction;
import com.eugenedevv.pesamint.entity.TransactionType;
import com.eugenedevv.pesamint.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by Eugene Devv on 23 Oct, 2023
 */

@DataJpaTest
public class TransactionRepositoryTest {

    Transaction transaction;
    User user;
    Date date = new Date();
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Eugene", null, null, null,
                null, null, null, null,
                null, null, null, null,
                null, null, null);


        transaction = new Transaction(1L, TransactionType.CREDIT, user, 1000.0,
                date, date.toString());

        userRepository.save(user);
        transactionRepository.save(transaction);
    }

    @AfterEach
    void tearDown() {
        user = null;

        userRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    // Test case SUCCESS
    @Test
    void testFindTransactionsByUserAndTransactionDateAndTransactionType_NotFound() {
        List<Transaction> transactionList = transactionRepository.findTransactionsByUserAndTransactionDateAndTransactionType(user, date, TransactionType.DEBIT);

       assertThat(transactionList.isEmpty()).isTrue();
    }

    // Test case FAILURE
    @Test
    void testFindTransactionsByUserAndTransactionDateAndTransactionType_Found() {
        List<Transaction> transactionList = transactionRepository.findTransactionsByUserAndTransactionDateAndTransactionType(user, date, TransactionType.CREDIT);

        assertThat(transactionList.get(0).getAmount()).isEqualTo(transaction.getAmount());
        assertThat(transactionList.get(0).getId()).isEqualTo(transaction.getId());
    }
}
