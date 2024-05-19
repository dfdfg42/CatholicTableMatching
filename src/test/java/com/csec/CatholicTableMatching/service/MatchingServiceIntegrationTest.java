package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.domain.MatchForm;
import com.csec.CatholicTableMatching.repository.MatchRepository;
import com.csec.CatholicTableMatching.security.domain.User;
import com.csec.CatholicTableMatching.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MatchingServiceIntegrationTest {

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Test
    //@Transactional
    public void testCreateMatch() {
        // Arrange
        User user1 = new User();
        user1.setLoginId("testuser1");
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
        potentialMatch.setLoginId("testuser2");
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

        matchRepository.save(result);

        // Assert
        assertNotNull(result, "The match result should not be null.");
        assertTrue(result.getUser1().isMatched(), "User1 should be marked as matched.");
        assertTrue(result.getUser2().isMatched(), "User2 should be marked as matched.");
        assertEquals(matchRepository.findAll().size(), 1, "Match not maked.");
    }
}