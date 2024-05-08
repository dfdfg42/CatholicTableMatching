package com.csec.CatholicTableMatching.security.repository;

import com.csec.CatholicTableMatching.security.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CUserRepositoryTest {

        @Autowired
        CUserRepository cUserRepository;

        @Test
        @DisplayName("유저 생성")
        void createUser() {
            // given
            User user1 = User.builder().id(1L).nickname("user1").build();
            User user2 = User.builder().id(1L).nickname("user1").build();

            // when
            User result1 = cUserRepository.save(user1);
            User result2 = cUserRepository.save(user2);

            // then
            assertEquals(result1.getNickname(), user1.getNickname());
            assertEquals(result2.getNickname(), user2.getNickname());
        }

        @Test
        @DisplayName("유저 리스트 조회")
        void getUserList() {
            // given
            User user1 = User.builder().id(1L).nickname("user1").build();
            User user2 = User.builder().id(1L).nickname("user1").build();
            cUserRepository.save(user1);
            cUserRepository.save(user2);

            // when
            List<User> result = cUserRepository.findAll();

            // then
            assertEquals(result.size(), 2);
        }
}