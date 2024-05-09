package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.domain.MatchForm;
import com.csec.CatholicTableMatching.repository.MatchFormRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class MatchFormService {

    @Autowired
    private MatchFormRepository matchFormRepository;

    public MatchForm saveMatchForm(MatchForm matchForm) {
        return matchFormRepository.save(matchForm);
    }

    public MatchForm getMatchFormById(Long id) {
        return matchFormRepository.findById(id).orElse(null);
    }
}
