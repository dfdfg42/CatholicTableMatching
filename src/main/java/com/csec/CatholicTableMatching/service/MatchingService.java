package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.domain.User;
import com.csec.CatholicTableMatching.repository.MatchRepository;
import com.csec.CatholicTableMatching.repository.PreferenceRepository;
import com.csec.CatholicTableMatching.repository.UserRepository;
import com.csec.CatholicTableMatching.repository.WatingSameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private Queue<User> waitingQueue = new LinkedList<>();
    private UserRepository userRepository;
    private PreferenceRepository preferenceRepository;
    private MatchRepository matchRepository;

    public boolean isWaitingQueueEmpty() {
        return waitingQueue.isEmpty();
    }

    public void addUserToQueue(User user) {
        waitingQueue.offer(user);
    }

    private WatingSameRepository waitingSameRepository;

    public void processMatchWithUserInQueue(User newUser) {
        if (!waitingQueue.isEmpty()) {
            User matchedUser = waitingQueue.poll();

            // Create and save a new match entity
            Match newMatch = new Match();
            newMatch.setUser1(matchedUser);
            newMatch.setUser2(newUser);
            newMatch.setMatchDate(new Date()); // Set the current date as the match date
            matchRepository.save(newMatch); // Save the match to the database using MatchRepository

            // Additional logic as needed
        }

    }
}
