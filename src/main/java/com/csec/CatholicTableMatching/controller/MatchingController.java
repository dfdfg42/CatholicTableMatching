package com.csec.CatholicTableMatching.controller;

import ch.qos.logback.core.model.Model;
import com.csec.CatholicTableMatching.domain.Customer;
import com.csec.CatholicTableMatching.domain.MatchForm;
import com.csec.CatholicTableMatching.service.MatchingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    private MatchForm matchForm;

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @RequestMapping("/match")
    public String MatchStart(){
        return "match_form";
    } // 매치 폼 화면이동

    @PostMapping("/match")
    public String findMatches(@ModelAttribute("matchForm") MatchForm matchForm) {
        matchingService.processMatchWithUserInQueue(matchForm);

        return "redirect:/match/" +matchForm.getUser();
    } // 매치 넣으면 매치현황판 으로 이동할수 있게

    @RequestMapping("/match/{userId}")
    public String MatchStatus(@PathVariable Customer customer){


        return "match_status";
    } // 매치 현황판
}
