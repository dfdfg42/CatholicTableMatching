package com.csec.CatholicTableMatching.security.repository;

import com.csec.CatholicTableMatching.security.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    Optional<User> findUserById(Long id);

    List<User> findAllByMatchedFalse();
    Optional<User> findUserByName(String name);

    @Query("SELECT u FROM CUSER u JOIN u.matchForm mf WHERE mf.foodType = :foodType AND u.gender = :preferGender AND u.matched = :matched")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<User> findMatchesByPreferences(String foodType, String preferGender, boolean matched);

}