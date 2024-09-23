package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.domain.MatchForm;
import com.csec.CatholicTableMatching.repository.MatchFormRepository;
import com.csec.CatholicTableMatching.repository.MatchRepository;
import com.csec.CatholicTableMatching.security.domain.User;
import com.csec.CatholicTableMatching.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class MatchingService {
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final MatchFormRepository matchFormRepository;
    private final SendMessageService sendMessageService;

    private final Object lock = new Object();

    // 오전 5시 59분에 Match와 MatchForm을 삭제하는 스케줄 작업
    @Scheduled(cron = "59 59 5 * * *")
    @Transactional
    public void clearMatchesAndMatchForms() {
        matchRepository.deleteAll();
        matchFormRepository.deleteAll();

        List<User> users = userRepository.findAll();

        for(User user : users){
            user.setMatched(false);
        }

        System.out.println("모든 매칭과 매칭 폼이 삭제되었습니다.");
    }

    // 오전 6시에 새로운 매칭을 생성하는 스케줄 작업
    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void createMatches() {
        createMatchForAllUsers();
        sendMessageService.sendAllSms();
        System.out.println("새로운 매칭이 생성되었습니다.");
    }

    @Transactional
    public void createMatchForAllUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.isMatched() && user.getMatchForm() != null) {
                createMatch(user);
            }
        }
    }

    @Transactional
    public Match createMatch(User user) {
        synchronized (lock) {
            MatchForm matchForm = user.getMatchForm();

            // 사용자의 선호 성별 설정
            String desiredGender;
            if ("동성".equals(matchForm.getPreferGender())) {
                desiredGender = user.getGender();
            } else if ("이성".equals(matchForm.getPreferGender())) {
                desiredGender = user.getGender().equals("M") ? "F" : "M";
            } else {
                // preferGender가 특정 성별을 의미하는 경우
                desiredGender = matchForm.getPreferGender();
            }

            // 사용자의 선호에 맞는 잠재적 매치 찾기
            List<User> potentialMatches = userRepository.findMatchesByPreferences(
                    matchForm.getFoodType(), desiredGender, false);

            User bestMatch = null;
            int maxOverlap = 0;

            for (User potentialMatch : potentialMatches) {
                if (!potentialMatch.isMatched() && !potentialMatch.getId().equals(user.getId())) {

                    // 상대방의 선호 성별 설정
                    MatchForm potentialMatchForm = potentialMatch.getMatchForm();
                    String potentialDesiredGender;
                    if ("동성".equals(potentialMatchForm.getPreferGender())) {
                        potentialDesiredGender = potentialMatch.getGender();
                    } else if ("이성".equals(potentialMatchForm.getPreferGender())) {
                        potentialDesiredGender = potentialMatch.getGender().equals("M") ? "F" : "M";
                    } else {
                        potentialDesiredGender = potentialMatchForm.getPreferGender();
                    }

                    // 현재 사용자와 잠재적 매치의 성별 호환성 확인
                    if (!user.getGender().equals(potentialDesiredGender)) {
                        continue; // 상대방의 선호에 맞지 않으면 패스
                    }

                    int overlapCount = getOverlapCount(matchForm.getTimeSlots(),
                            potentialMatchForm.getTimeSlots());

                    if (overlapCount > maxOverlap) {
                        bestMatch = potentialMatch;
                        maxOverlap = overlapCount;
                    }
                }
            }

            if (bestMatch != null) {
                List<Integer> shorterTimeSlots = matchForm.getTimeSlots().size() <
                        bestMatch.getMatchForm().getTimeSlots().size()
                        ? matchForm.getTimeSlots()
                        : bestMatch.getMatchForm().getTimeSlots();

                Match bestUserMatch = new Match(user, bestMatch, new Date(),
                        shorterTimeSlots.toString(), matchForm.getFoodType());

                bestMatch.setMatched(true);
                user.setMatched(true);

                userRepository.save(bestMatch);
                userRepository.save(user);
                return matchRepository.save(bestUserMatch);
            }

            return null;
        }
    }



    private int getOverlapCount(List<Integer> userTimeSlots, List<Integer> potentialMatchTimeSlots) {
        Set<Integer> userTimeSet = new HashSet<>(userTimeSlots);
        userTimeSet.retainAll(potentialMatchTimeSlots);
        return userTimeSet.size();
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
