package com.eugenedevv.pesamint.repository;

import com.eugenedevv.pesamint.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by Eugene Devv on 23 Oct, 2023
 */

@DataJpaTest
public class UserRepositoryTest {

    User user;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Eugene", "Odhiambo", null, null,
                null, null, "12345678", null,
                "eugene@example.com", "+254712345678", "+254787654321", null,
                null, null, null);

        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        user = null;
        userRepository.deleteAll();
    }

    // existsByEmail tests
    @Test
    void testExistsByEmail_Found() {
        boolean existsByEmail = userRepository.existsByEmail("eugene@example.com");
        assertThat(existsByEmail).isTrue();
    }

    @Test
    void testExistsByEmail_NotFound() {
        boolean existsByEmail = userRepository.existsByEmail("example@gmail.com");
        assertThat(existsByEmail).isFalse();
    }

    //    existsByAccountNumber
    @Test
    void testExistsByAccountNumber_Found() {
        boolean existsByAccountNumber = userRepository.existsByAccountNumber("12345678");
        assertThat(existsByAccountNumber).isTrue();
    }

    @Test
    void testExistsByAccountNumber_NotFound() {
        boolean existsByAccountNumber = userRepository.existsByEmail("87654321");
        assertThat(existsByAccountNumber).isFalse();
    }

    //    existsByPhoneNumber
    @Test
    void testExistsByPhoneNumber_Found() {
        boolean existsByPhoneNumber = userRepository.existsByPhoneNumber("+254712345678");
        assertThat(existsByPhoneNumber).isTrue();
    }

    @Test
    void testExistsByPhoneNumber_NotFound() {
        boolean existsByPhoneNumber = userRepository.existsByPhoneNumber("+254787654321");
        assertThat(existsByPhoneNumber).isFalse();
    }

    //    existsByAlternativePhoneNumber
    @Test
    void testExistsByAlternativePhoneNumber_Found() {
        boolean existsByAlternativePhoneNumber = userRepository.existsByAlternativePhoneNumber("+254787654321");
        assertThat(existsByAlternativePhoneNumber).isTrue();
    }

    @Test
    void testExistsByAlternativePhoneNumber_NotFound() {
        boolean existsByAlternativePhoneNumber = userRepository.existsByAlternativePhoneNumber("+254712345678");
        assertThat(existsByAlternativePhoneNumber).isFalse();
    }

    //    findByAccountNumber
    @Test
    void testFindByAccountNumber_Found() {
        User filteredUser = userRepository.findByAccountNumber("12345678");
        assertThat(filteredUser.getId()).isEqualTo(user.getId());
    }

    @Test
    void testFindByAccountNumber_NotFound() {
        User filteredUser = userRepository.findByAccountNumber("87654321");
        assertThat(filteredUser).isNull();
    }

}
