package com.csec.CatholicTableMatching.security.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserRoleTest {

    @Test
    @DisplayName("유저권한생성")
    void createUserRole() {
        // given, when
        UserRole userRole = UserRole.USER;

        // then
        Assertions.assertEquals(userRole, UserRole.USER);
    }
}
