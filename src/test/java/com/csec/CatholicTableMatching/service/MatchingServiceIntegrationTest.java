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
        User user = new User();
        user.setUsername("user1");
        user.setGender("M");
        user.setMatched(false);
        userRepository.save(user);

        User potentialMatch = new User();
        potentialMatch.setUsername("user2");
        potentialMatch.setGender("F");
        potentialMatch.setMatched(false);
        userRepository.save(potentialMatch);

        MatchForm matchForm = new MatchForm();
        matchForm.setFoodType("Italian");
        matchForm.setTimeSlot("Evening");
        matchForm.setPreferGender("F");
        user.setMatchForm(matchForm);
        potentialMatch.setMatchForm(matchForm);

        // Act
        Match result = matchingService.createMatch(user);

        // Assert
        assertNotNull(result, "The match result should not be null.");
        assertTrue(result.getUser1().isMatched(), "User1 should be marked as matched.");
        assertTrue(result.getUser2().isMatched(), "User2 should be marked as matched.");
    }
}