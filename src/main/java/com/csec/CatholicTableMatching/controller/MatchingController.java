package com.csec.CatholicTableMatching.controller;

import ch.qos.logback.core.model.Model;
import com.csec.CatholicTableMatching.domain.Customer;
import com.csec.CatholicTableMatching.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MatchingController {
    @Autowired
    private MatchingService matchingService;

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @PostMapping("/match")
    public String findMatches(@ModelAttribute Customer user, Model model) {
        // 대기열에 사람이 있는지 확인
        boolean isWaitingQueueEmpty = matchingService.isWaitingQueueEmpty();

        if (!isWaitingQueueEmpty) {
            // 대기열에 사람이 있다면, 매칭 로직을 수행하고 매칭 성공 페이지로 리디렉션
            matchingService.processMatchWithUserInQueue(user);
            return "match_success"; // 매칭 성공 화면으로 이동
        } else {
            // 대기열이 비어 있다면, 사용자를 대기열에 추가
            matchingService.addUserToQueue(user);
            return "match_waiting"; // 매칭 중 화면으로 이동
        }
    }
}
