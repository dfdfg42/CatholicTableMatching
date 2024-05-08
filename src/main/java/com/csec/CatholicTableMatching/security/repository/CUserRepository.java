package com.csec.CatholicTableMatching.security.repository;

import com.csec.CatholicTableMatching.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CUserRepository extends JpaRepository<User, Long> {
}