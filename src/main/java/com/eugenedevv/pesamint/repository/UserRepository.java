package com.eugenedevv.pesamint.repository;

import com.eugenedevv.pesamint.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByAccountNumber(String accountNumber);
    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByAlternativePhoneNumber(String altPhoneNumber);
}
