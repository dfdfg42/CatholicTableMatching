package com.csec.CatholicTableMatching.security.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    @DisplayName("유저생성")
    void createUser() {
        // given, when
        User user = User.builder().id(1L).loginId("tester").build();

        // then
        Assertions.assertEquals(user.getId(), 1L);
        Assertions.assertEquals(user.getLoginId(), "tester");
    }

}
