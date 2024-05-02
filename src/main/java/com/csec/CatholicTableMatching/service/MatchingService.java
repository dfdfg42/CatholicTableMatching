package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.domain.Customer;
import com.csec.CatholicTableMatching.repository.MatchRepository;
import com.csec.CatholicTableMatching.repository.PreferenceRepository;
import com.csec.CatholicTableMatching.repository.UserRepository;
import com.csec.CatholicTableMatching.repository.WatingSameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private Queue<Customer> waitingQueue = new LinkedList<>();
    private UserRepository userRepository;
    private PreferenceRepository preferenceRepository;
    private MatchRepository matchRepository;

    public boolean isWaitingQueueEmpty() {
        return waitingQueue.isEmpty();
    }

    public void addUserToQueue(Customer user) {
        waitingQueue.offer(user);
    }

    private WatingSameRepository waitingSameRepository;

    public void processMatchWithUserInQueue(Customer newUser) {
        if (!waitingQueue.isEmpty()) {
            Customer matchedUser = waitingQueue.poll();

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
