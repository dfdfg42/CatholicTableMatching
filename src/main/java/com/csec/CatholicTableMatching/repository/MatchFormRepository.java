package com.csec.CatholicTableMatching.repository;


import com.csec.CatholicTableMatching.domain.MatchForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchFormRepository extends JpaRepository<MatchForm, Long> {

}