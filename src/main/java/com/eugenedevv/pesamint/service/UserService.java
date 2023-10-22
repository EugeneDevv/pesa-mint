package com.eugenedevv.pesamint.service;

import com.eugenedevv.pesamint.dto.BankResponse;
import com.eugenedevv.pesamint.dto.CreditDebitRequest;
import com.eugenedevv.pesamint.dto.EnquiryRequest;
import com.eugenedevv.pesamint.dto.UserRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse detailsEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
}
