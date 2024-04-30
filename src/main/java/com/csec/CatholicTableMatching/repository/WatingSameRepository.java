package com.csec.CatholicTableMatching.repository;

import com.csec.CatholicTableMatching.domain.WaitingSame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.beans.JavaBean;

public interface WatingSameRepository extends JpaRepository<WaitingSame,Long> {

    @Query("SELECT COUNT(w) FROM WaitingSame w")
    long countAllWaitingSame();
}
