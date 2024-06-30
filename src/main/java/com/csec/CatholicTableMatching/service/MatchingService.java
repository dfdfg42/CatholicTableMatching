package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.BinarySearchTree;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class MatchingService {

    private final ConcurrentMap<String,BinarySearchTree> indexHashTable = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final MatchFormRepository matchFormRepository;

    // lock을 이용한 동시성 제어
    private final Lock lock = new ReentrantLock();

    public MatchingService(UserRepository userRepository, MatchRepository matchRepository, MatchFormRepository matchFormRepository) {
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
        this.matchFormRepository = matchFormRepository;
        // 초기화 (각 특성에 대해 이진 탐색 트리 생성)
        indexHashTable.put("foodType", new BinarySearchTree());
        indexHashTable.put("timeSlot", new BinarySearchTree());
        indexHashTable.put("preferGender", new BinarySearchTree());
    }

    @Transactional
    public void createMatchForAllUsers() {
        List<User> users = userRepository.findAll();
        List<Match> matches = new ArrayList<>();
        for (User user : users) {
            if (!user.isMatched() && user.getMatchForm() != null) {
                addToIndex(user);
                Match match = createMatch(user);
                if (match != null) {
                    matches.add(match);
                }
            }
        }
        // return matches; // 모든 매치 결과 반환 (필요 시)
    }

    private void addToIndex(User user) {
        MatchForm matchForm = user.getMatchForm();
        indexHashTable.get("foodType").insert(matchForm.getFoodType(), user);
        indexHashTable.get("timeSlot").insert(matchForm.getTimeSlot(), user);
        indexHashTable.get("preferGender").insert(matchForm.getPreferGender(), user);
    }

    private void removeFromIndex(User user) {
        MatchForm matchForm = user.getMatchForm();
        indexHashTable.get("foodType").remove(matchForm.getFoodType(), user);
        indexHashTable.get("timeSlot").remove(matchForm.getTimeSlot(), user);
        indexHashTable.get("preferGender").remove(matchForm.getPreferGender(), user);
    }

    @Transactional
    public Match createMatch(User user) {
        lock.lock(); // 동시성 제어를 위한 lock
        try {
            MatchForm matchForm = user.getMatchForm();
            List<User> potentialMatches = findMatchesByPreferences(matchForm);

            for (User potentialMatch : potentialMatches) {
                if (!potentialMatch.isMatched() && !potentialMatch.getId().equals(user.getId())
                        && !potentialMatch.getGender().equals(user.getGender())) {
                    Match match = new Match(user, potentialMatch, new Date(), matchForm.getTimeSlot(), matchForm.getFoodType());

                    potentialMatch.setMatched(true);
                    user.setMatched(true);

                    removeFromIndex(user);
                    removeFromIndex(potentialMatch);

                    userRepository.save(potentialMatch);
                    userRepository.save(user);
                    return matchRepository.save(match);
                }
            }
            return null; // No match found
        } finally {
            lock.unlock(); // 락 해제
        }
    }

    private List<User> findMatchesByPreferences(MatchForm matchForm) {
        List<User> foodTypeMatches = indexHashTable.get("foodType").search(matchForm.getFoodType());
        List<User> timeSlotMatches = indexHashTable.get("timeSlot").search(matchForm.getTimeSlot());
        List<User> preferGenderMatches = indexHashTable.get("preferGender").search(matchForm.getPreferGender());

        // 교집합을 찾아야 함 (세 가지 조건을 모두 만족하는 사용자)
        // 여기서는 간단히 세 목록의 공통 항목을 찾는 로직을 구현
        return intersectUsers(foodTypeMatches, timeSlotMatches, preferGenderMatches);
    }

    private List<User> intersectUsers(List<User>... lists) {
        // 교집합을 찾는 로직 (여기서는 간단히 리스트 교집합을 구현)
        if (lists.length == 0) return new ArrayList<>();
        List<User> intersection = new ArrayList<>(lists[0]);
        for (List<User> list : lists) {
            intersection.retainAll(list);
        }
        return intersection;
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
