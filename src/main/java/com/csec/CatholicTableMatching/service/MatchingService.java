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

    private final HashMap<String, List<User>> userTable = new HashMap<>();
    private final HashMap<String, List<User>> unmatchedUserTable = new HashMap<>();
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final MatchFormRepository matchFormRepository;

    private final Lock lock = new ReentrantLock();

    public MatchingService(UserRepository userRepository, MatchRepository matchRepository, MatchFormRepository matchFormRepository) {
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
        this.matchFormRepository = matchFormRepository;
        initializeTableWithUsers();
    }

    private void initializeTableWithUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getMatchForm() != null) {
                addToTable(user);
                if (!user.isMatched()) {
                    addToUnmatchedTable(user);
                }
            }
        }
    }

    @Transactional
    public void createMatchForAllUsers() {
        List<User> users = userRepository.findAll();
        List<Match> matches = new ArrayList<>();
        for (User user : users) {
            if (!user.isMatched() && user.getMatchForm() != null) {
                addToTable(user);
                addToUnmatchedTable(user);
                Match match = createMatch(user);
                if (match != null) {
                    matches.add(match);
                }
            }
        }
    }

    private void addToTable(User user) {
        MatchForm matchForm = user.getMatchForm();
        String key = generateKey(matchForm.getFoodType(), matchForm.getTimeSlot(), matchForm.getPreferGender());
        userTable.computeIfAbsent(key, k -> new ArrayList<>()).add(user);
    }

    private void addToUnmatchedTable(User user) {
        MatchForm matchForm = user.getMatchForm();
        String key = generateKey(matchForm.getFoodType(), matchForm.getTimeSlot(), matchForm.getPreferGender());
        unmatchedUserTable.computeIfAbsent(key, k -> new ArrayList<>()).add(user);
    }

    private void removeFromTable(User user) {
        MatchForm matchForm = user.getMatchForm();
        String key = generateKey(matchForm.getFoodType(), matchForm.getTimeSlot(), matchForm.getPreferGender());
        List<User> users = userTable.get(key);
        if (users != null) {
            users.remove(user);
            if (users.isEmpty()) {
                userTable.remove(key);
            }
        }
    }

    private void removeFromUnmatchedTable(User user) {
        MatchForm matchForm = user.getMatchForm();
        String key = generateKey(matchForm.getFoodType(), matchForm.getTimeSlot(), matchForm.getPreferGender());
        List<User> users = unmatchedUserTable.get(key);
        if (users != null) {
            users.remove(user);
            if (users.isEmpty()) {
                unmatchedUserTable.remove(key);
            }
        }
    }

    private String generateKey(String foodType, String timeSlot, String preferGender) {
        return foodType + "|" + timeSlot + "|" + preferGender;
    }

    @Transactional
    public Match createMatch(User user) {
        lock.lock();
        try {
            MatchForm matchForm = user.getMatchForm();
            String key = generateKey(matchForm.getFoodType(), matchForm.getTimeSlot(), matchForm.getPreferGender());
            List<User> potentialMatches = unmatchedUserTable.getOrDefault(key, new ArrayList<>());

            for (User potentialMatch : potentialMatches) {
                if (!potentialMatch.isMatched() && !potentialMatch.getId().equals(user.getId())
                        && !potentialMatch.getGender().equals(user.getGender())) {
                    Match match = new Match(user, potentialMatch, new Date(), matchForm.getTimeSlot(), matchForm.getFoodType());

                    potentialMatch.setMatched(true);
                    user.setMatched(true);

                    removeFromTable(user);
                    removeFromTable(potentialMatch);
                    removeFromUnmatchedTable(user);
                    removeFromUnmatchedTable(potentialMatch);

                    userRepository.save(potentialMatch);
                    userRepository.save(user);
                    return matchRepository.save(match);
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public List<Match> matchResult() {
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
