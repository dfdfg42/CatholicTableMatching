package com.csec.CatholicTableMatching.security.repository;

import com.csec.CatholicTableMatching.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
}