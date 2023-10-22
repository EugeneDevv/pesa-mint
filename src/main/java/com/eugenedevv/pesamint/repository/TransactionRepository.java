package com.eugenedevv.pesamint.repository;

import com.eugenedevv.pesamint.entity.Transaction;
import com.eugenedevv.pesamint.entity.TransactionType;
import com.eugenedevv.pesamint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by Eugene Devv on 22 Oct, 2023
 */
public interface TransactionRepository  extends JpaRepository<Transaction, Long> {
    List<Transaction> findTransactionsByUserAndTransactionDateAndTransactionType(
            @Param("user") User user,
            @Param("transactionDate") Date transactionDate,
            @Param("transactionType") TransactionType transactionType
    );
}
