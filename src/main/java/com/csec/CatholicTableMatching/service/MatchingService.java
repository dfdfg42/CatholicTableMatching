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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final Lock lock = new ReentrantLock(); //락 걸어 버리기
    private final MatchFormRepository matchFormRepository;

    @Transactional
    public void createMatchForAllUsers() {
        List<User> users = userRepository.findAll();
        List<Match> matches = new ArrayList<>();
        for (User user : users) {
            if(!user.isMatched() && !(user.getMatchForm()==null) ){
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
        lock.lock(); //락 함걸어봄
        try {
            MatchForm matchForm = user.getMatchForm();
            List<User> potentialMatches = userRepository.findMatchesByPreferences(
                    matchForm.getFoodType(), matchForm.getTimeSlot(), matchForm.getPreferGender(), false);

            for (User potentialMatch : potentialMatches) {
                if (!potentialMatch.isMatched() && !potentialMatch.getId().equals(user.getId())
                        && !potentialMatch.getGender().equals(user.getGender())) {
                    Match match = new Match(user, potentialMatch, new Date(), matchForm.getTimeSlot(), matchForm.getFoodType());

                    potentialMatch.setMatched(true);
                    user.setMatched(true);

                    userRepository.save(potentialMatch);
                    userRepository.save(user);
                    return matchRepository.save(match);
                }
            }
            return null; // No match found
        } finally {
            lock.unlock(); // 락 임시
        }
    }

    @Transactional
    public List<Match> MatchResult(){
        List<Match> matches = matchRepository.findAll();
        return matches;
    }

    @Transactional
    public User findPartner(User user) {
        User partner = matchRepository.findPartnerByUser1(user);
        if (partner == null) {
            partner = matchRepository.findPartnerByUser2(user);
        }
        return partner;
    }




}

