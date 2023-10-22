package com.eugenedevv.pesamint.service;

import com.eugenedevv.pesamint.dto.BankResponse;
import com.eugenedevv.pesamint.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
}
