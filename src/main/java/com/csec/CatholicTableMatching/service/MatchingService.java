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
    private final Lock lock = new ReentrantLock(); // 락 걸어버리기
    private final MatchFormRepository matchFormRepository;

    @Transactional
    public void createMatchForAllUsers() {
        List<User> users = userRepository.findAll();
        List<Match> matches = new ArrayList<>();
        for (User user : users) {
            if (!user.isMatched() && !(user.getMatchForm() == null)) {
                Match match = createMatch(user);
                if (match != null) {
                    matches.add(match);
                }
            }
        }
    }

    @Transactional
    public Match createMatch(User user) {
        lock.lock(); // 락 걸기
        try {
            MatchForm matchForm = user.getMatchForm();
            List<User> potentialMatches = userRepository.findMatchesByPreferences(
                    matchForm.getFoodType(), matchForm.getPreferGender(), false);

            User bestMatch = null;
            int maxOverlap = 0;

            for (User potentialMatch : potentialMatches) {
                if (!potentialMatch.isMatched() && !potentialMatch.getId().equals(user.getId())
                        && !potentialMatch.getGender().equals(user.getGender())) {

                    // 시간대 겹치는 부분을 계산
                    int overlapCount = getOverlapCount(matchForm.getTimeSlots(), potentialMatch.getMatchForm().getTimeSlots());

                    // 가장 많은 시간이 겹치는 유저를 찾는다.
                    if (overlapCount > maxOverlap) {
                        bestMatch = potentialMatch;
                        maxOverlap = overlapCount;
                    }
                }
            }

            if (bestMatch != null) {
                Match bestUserMatch = new Match(user, bestMatch, new Date(), matchForm.getTimeSlot(), matchForm.getFoodType());

                bestMatch.setMatched(true);
                user.setMatched(true);

                userRepository.save(bestMatch);
                userRepository.save(user);
                return matchRepository.save(bestUserMatch);
            }

            return null; // 매칭되는 유저가 없는 경우
        } finally {
            lock.unlock(); // 락 해제
        }
    }

    // 두 유저의 시간 리스트에서 겹치는 시간이 몇 개인지 계산하는 메서드
    private int getOverlapCount(List<Integer> userTimeSlots, List<Integer> potentialMatchTimeSlots) {
        int overlapCount = 0;
        for (Integer timeSlot : userTimeSlots) {
            if (potentialMatchTimeSlots.contains(timeSlot)) {
                overlapCount++;
            }
        }
        return overlapCount;
    }

    @Transactional
    public List<Match> MatchResult() {
        return matchRepository.findAll();
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
