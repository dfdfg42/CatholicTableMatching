package com.csec.CatholicTableMatching.repository;

import com.csec.CatholicTableMatching.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN u.matchForm mf WHERE mf.foodType = :foodType AND mf.timeSlot = :timeSlot AND u.gender = :preferGender AND u.matched = :matched")
    List<User> findMatchesByPreferences(String foodType, String timeSlot, String preferGender, boolean matched);
}
