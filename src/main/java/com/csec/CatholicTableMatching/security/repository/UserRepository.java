package com.csec.CatholicTableMatching.security.repository;

import com.csec.CatholicTableMatching.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    Optional<User> findUserById(Long id);

    @Query("SELECT u FROM CUSER u JOIN u.matchForm mf WHERE mf.foodType = :foodType AND mf.timeSlot = :timeSlot AND u.gender = :preferGender AND u.matched = :matched")
    List<User> findMatchesByPreferences(String foodType, String timeSlot, String preferGender, boolean matched);
}