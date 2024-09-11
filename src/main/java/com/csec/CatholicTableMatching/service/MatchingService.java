package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.controller.MessageController;
import com.csec.CatholicTableMatching.domain.*;
import com.csec.CatholicTableMatching.repository.MatchFormRepository;
import com.csec.CatholicTableMatching.repository.MatchRepository;
import com.csec.CatholicTableMatching.security.domain.User;
import com.csec.CatholicTableMatching.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.Message;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class MatchingService {
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final Lock lock = new ReentrantLock(); // 락 걸어버리기
    private final MatchFormRepository matchFormRepository;
    private final MessageController messageController;


    // 오전 5시 59분에 Match와 MatchForm을 삭제하는 스케줄 작업
    @Scheduled(cron = "59 59 5 * * *") // 매일 오전 5시 59분에 실행
    @Transactional
    public void clearMatchesAndMatchForms() {
        matchRepository.deleteAll();
        matchFormRepository.deleteAll();
        System.out.println("모든 매칭과 매칭 폼이 삭제되었습니다.");
    }

    // 오전 6시에 새로운 매칭을 생성하는 스케줄 작업
    @Scheduled(cron = "0 0 6 * * *") // 매일 오전 6시에 실행
    @Transactional
    public void createMatches() {
        createMatchForAllUsers();  // 기존에 작성된 매칭 생성 로직을 호출

        messageController.sendAllSms();
        System.out.println("새로운 매칭이 생성되었습니다.");


    }






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

            log.info("Final max overlap for user {}: {}", user.getName(), maxOverlap);


            if (bestMatch != null) {
                // 두 유저의 timeSlots 중 더 짧은 배열을 선택
                List<Integer> shorterTimeSlots = matchForm.getTimeSlots().size() < bestMatch.getMatchForm().getTimeSlots().size()
                        ? matchForm.getTimeSlots()
                        : bestMatch.getMatchForm().getTimeSlots();

                // Match 객체에 더 짧은 timeSlots 배열을 저장
                Match bestUserMatch = new Match(user, bestMatch, new Date(), shorterTimeSlots.toString(), matchForm.getFoodType());

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
