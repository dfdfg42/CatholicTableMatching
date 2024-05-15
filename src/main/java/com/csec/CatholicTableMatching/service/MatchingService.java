package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.domain.*;
import com.csec.CatholicTableMatching.repository.MatchFormRepository;
import com.csec.CatholicTableMatching.repository.MatchRepository;
import com.csec.CatholicTableMatching.security.domain.User;
import com.csec.CatholicTableMatching.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    private final MatchFormRepository matchFormRepository;

    @Transactional
    public void createMatchForAllUsers() {
        List<User> users = userRepository.findAll();
        List<Match> matches = new ArrayList<>();
        for (User user : users) {
            if(!user.isMatched()){
                Match match = createMatch(user);
                if (match != null) {
                    matches.add(match);
                }
            }
        }
      /*  return matches; // 모든 매치 결과 반환*/
    }

    @Transactional
    public Match createMatch(User user) {
        MatchForm matchForm = user.getMatchForm();
        List<User> potentialMatches = userRepository.findMatchesByPreferences(
                matchForm.getFoodType(), matchForm.getTimeSlot(), matchForm.getPreferGender(), false);

        for (User potentialMatch : potentialMatches) {
            if (!potentialMatch.isMatched() && !potentialMatch.getId().equals(user.getId()) &&!potentialMatch.getGender().equals(user.getGender()) ) {
                Match match = new Match(user,potentialMatch,new Date(),matchForm.getTimeSlot(),matchForm.getFoodType());

                potentialMatch.setMatched(true);
                user.setMatched(true);

                userRepository.save(potentialMatch);
                userRepository.save(user);
                return matchRepository.save(match);
            }
        }
        return null; // No match found
    }

    @Transactional
    public List<Match> MatchResult(){
        List<Match> matches = matchRepository.findAll();
        return matches;
    }




}

