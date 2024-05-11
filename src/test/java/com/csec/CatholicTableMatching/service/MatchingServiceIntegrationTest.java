package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.domain.MatchForm;
import com.csec.CatholicTableMatching.domain.User;
import com.csec.CatholicTableMatching.repository.MatchFormRepository;
import com.csec.CatholicTableMatching.repository.MatchRepository;
import com.csec.CatholicTableMatching.repository.UserRepository;
import com.csec.CatholicTableMatching.service.MatchingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
@SpringBootTest
public class MatchingServiceIntegrationTest {

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Test
    @Transactional
    public void testCreateMatch() {
        // Arrange
        User user1 = new User();
        user1.setUsername("user1");
        user1.setGender("M");
        user1.setMatched(false);
        MatchForm matchForm1 = new MatchForm();
        matchForm1.setFoodType("Italian");
        matchForm1.setTimeSlot("Evening");
        matchForm1.setPreferGender("F");
        //matchFormRepository.save(matchForm1);
        user1.setMatchForm(matchForm1);
        userRepository.save(user1);

        User potentialMatch = new User();
        potentialMatch.setUsername("user2");
        potentialMatch.setGender("F");
        potentialMatch.setMatched(false);
        MatchForm matchForm2 = new MatchForm();
        matchForm2.setFoodType("Italian");
        matchForm2.setTimeSlot("Evening");
        matchForm2.setPreferGender("F");
        //matchFormRepository.save(matchForm2);
        potentialMatch.setMatchForm(matchForm2);
        userRepository.save(potentialMatch);

        // Act
        Match result = matchingService.createMatch(user1);

        // Assert
        assertNotNull(result, "The match result should not be null.");
        assertTrue(result.getUser1().isMatched(), "User1 should be marked as matched.");
        assertTrue(result.getUser2().isMatched(), "User2 should be marked as matched.");
    }
}