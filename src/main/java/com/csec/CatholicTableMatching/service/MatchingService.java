package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.domain.*;
import com.csec.CatholicTableMatching.repository.MatchFormRepository;
import com.csec.CatholicTableMatching.repository.MatchRepository;
import com.csec.CatholicTableMatching.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private UserRepository userRepository;
    private MatchRepository matchRepository;

    private MatchFormRepository matchFormRepository;

    @Transactional
    public Match createMatch(User user) {
        MatchForm matchForm = user.getMatchForm();
        List<User> potentialMatches = userRepository.findMatchesByPreferences(
                matchForm.getFoodType(), matchForm.getTimeSlot(), matchForm.getPreferGender(), false);

        for (User potentialMatch : potentialMatches) {
            if (!potentialMatch.isMatched() && !potentialMatch.getId().equals(user.getId())) {
                Match match = new Match();
                match.setUser1(user);
                match.setUser2(potentialMatch);
                match.setMatchDate(new Date());
                match.setTimeSlot(matchForm.getTimeSlot());
                match.setFoodType(matchForm.getFoodType());

                potentialMatch.setMatched(true);
                user.setMatched(true);

                userRepository.save(potentialMatch);
                userRepository.save(user);
                return matchRepository.save(match);
            }
        }
        return null; // No match found
    }


}

