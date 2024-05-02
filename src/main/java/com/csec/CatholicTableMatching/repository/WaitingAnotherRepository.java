package com.csec.CatholicTableMatching.repository;

import com.csec.CatholicTableMatching.domain.WaitingAnother;
import com.csec.CatholicTableMatching.domain.WaitingSame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WaitingAnotherRepository extends JpaRepository<WaitingAnother,Long> {

    @Query("SELECT COUNT(w) FROM WaitingSame w")
    long countAllWaitingSame();
}
