package com.csec.CatholicTableMatching.repository;

import com.csec.CatholicTableMatching.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Customer, Long> {
}
