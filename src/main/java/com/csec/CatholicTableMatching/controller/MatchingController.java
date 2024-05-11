package com.csec.CatholicTableMatching.controller;

import com.csec.CatholicTableMatching.domain.User;
import com.csec.CatholicTableMatching.domain.MatchForm;
import com.csec.CatholicTableMatching.service.MatchingService;
import com.csec.CatholicTableMatching.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MatchingController {

    private MatchingService matchingService;

    private UserService userService;

    private MatchForm matchForm;

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @RequestMapping("/match")
    public String MatchStart(@ModelAttribute User user){
        return "match_form";
    } // 매치 폼 화면이동

    @PostMapping("/match")
    public String findMatches(@ModelAttribute("matchForm") MatchForm matchForm) {
        Long userId = matchForm.getUser().getId(); // 가정: getUser()가 사용자 ID를 반환한다.
        return "redirect:/match/" + userId;
    } // 매치 넣으면 매치현황판 으로 이동할수 있게

    @RequestMapping("/match/{userId}")
    public String MatchStatus(@PathVariable("userId") Long userId, Model model){
        User user = userService.findUserById(userId);
        model.addAttribute("customer", user);
        return "match_status";

    } // 매치 현황판



}
