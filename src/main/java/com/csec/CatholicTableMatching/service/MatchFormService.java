package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.domain.MatchForm;
import com.csec.CatholicTableMatching.repository.MatchFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor
public class MatchFormService {


    private final MatchFormRepository matchFormRepository;

    public MatchForm saveMatchForm(MatchForm matchForm) {
        return matchFormRepository.save(matchForm);
    }

    public MatchForm getMatchFormById(Long id) {
        return matchFormRepository.findById(id).orElse(null);
    }
}
