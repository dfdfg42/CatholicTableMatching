package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.domain.User;
import com.csec.CatholicTableMatching.repository.MatchRepository;
import com.csec.CatholicTableMatching.repository.PreferenceRepository;
import com.csec.CatholicTableMatching.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PreferenceRepository preferenceRepository;
    @Autowired
    private MatchRepository matchRepository;

    public List<Match> findMatchesForUser(User user) {
        // 매칭 로직 구현

    }
}
