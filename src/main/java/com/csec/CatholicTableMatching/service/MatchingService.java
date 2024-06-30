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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class MatchingService {

    private final BinarySearchTree binarySearchTree = new BinarySearchTree();
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final MatchFormRepository matchFormRepository;

    // lock을 이용한 동시성 제어
    private final Lock lock = new ReentrantLock();

    public MatchingService(UserRepository userRepository, MatchRepository matchRepository, MatchFormRepository matchFormRepository) {
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
        this.matchFormRepository = matchFormRepository;
        initializeTreeWithUsers();
    }

    // 모든 유저를 트리에 삽입하는 메서드
    private void initializeTreeWithUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.isMatched() && user.getMatchForm() != null) {
                addToIndex(user);
            }
        }
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
        binarySearchTree.insert(matchForm.getFoodType(), matchForm.getTimeSlot(), matchForm.getPreferGender(), user);
    }

    private void removeFromIndex(User user) {
        MatchForm matchForm = user.getMatchForm();
        binarySearchTree.remove(matchForm.getFoodType(), matchForm.getTimeSlot(), matchForm.getPreferGender(), user);
    }

    @Transactional
    public Match createMatch(User user) {
        lock.lock(); // 동시성 제어를 위한 lock
        try {
            MatchForm matchForm = user.getMatchForm();
            List<User> potentialMatches = binarySearchTree.search(matchForm.getFoodType(), matchForm.getTimeSlot(), matchForm.getPreferGender());

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
