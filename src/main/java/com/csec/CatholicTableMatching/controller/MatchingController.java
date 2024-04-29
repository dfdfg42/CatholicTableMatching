package com.csec.CatholicTableMatching.controller;

import ch.qos.logback.core.model.Model;
import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.domain.User;
import com.csec.CatholicTableMatching.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MatchingController {
    @Autowired
    private MatchingService matchingService;

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @PostMapping("/match")
    public String findMatches(@ModelAttribute User user, Model model) {
        List<Match> matches = matchingService.findMatchesForUser(user);
        model.addAttribute("matches", matches);
        return "matchResults";
    }
}
