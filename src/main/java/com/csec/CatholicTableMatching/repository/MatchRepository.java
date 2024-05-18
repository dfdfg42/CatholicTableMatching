package com.csec.CatholicTableMatching.repository;

import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository  extends JpaRepository<Match, Long> {
    @Query("SELECT m.user2 FROM Match m WHERE m.user1 = :user")
    User findPartnerByUser1(@Param("user") User user);

    @Query("SELECT m.user1 FROM Match m WHERE m.user2 = :user")
    User findPartnerByUser2(@Param("user") User user);

}
