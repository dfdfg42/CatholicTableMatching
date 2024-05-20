package com.csec.CatholicTableMatching.controller;

import com.csec.CatholicTableMatching.api.NyumtolicService;
import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.domain.MatchForm;
import com.csec.CatholicTableMatching.repository.MatchRepository;
import com.csec.CatholicTableMatching.security.service.EncryptService;
import com.csec.CatholicTableMatching.service.SendMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final SendMessageService sendMessageService;
    private final EncryptService encryptService;
    private final MatchRepository matchRepository;
    private final NyumtolicService nyumtolicService;

    @GetMapping("/sendSms/{matchId}")
    public String sendSms(@PathVariable Long matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match not found"));
        if (!match.isSended()) {
            sendUserDetailsToEachOther(match);
        }
        match.setSended(true);
        matchRepository.save(match);
        return "redirect:/matchResult";
    }

    @GetMapping("/sendAllSms")
    public String sendAllSms() {
        List<Match> matches = matchRepository.findAll();

        for (Match match : matches) {
            if (!match.isSended()) {
                sendUserDetailsToEachOther(match);
                match.setSended(true);
                matchRepository.save(match);
            }
        }
        return "redirect:/matchResult";
    }

    private void sendUserDetailsToEachOther(Match match) {
        String user1Phone = encryptService.decrypt(match.getUser1().getPhoneNum());
        String user2Phone = encryptService.decrypt(match.getUser2().getPhoneNum());

        String user1Message = formatMessage(
                match.getUser2().getName(),
                match.getUser2().getGender(),
                user2Phone,
                getMatchedCategories(match.getUser1().getMatchForm())
        );

        String user2Message = formatMessage(
                match.getUser1().getName(),
                match.getUser1().getGender(),
                user1Phone,
                getMatchedCategories(match.getUser2().getMatchForm())
        );

        sendMessageService.sendMessage("01039077292", user1Phone, user1Message);
        sendMessageService.sendMessage("01039077292", user2Phone, user2Message);
    }

    private String formatMessage(String name, String gender, String phone, String categories) {
        return String.format(
                "만냠 - 같이 밥먹을 사람 구하기\n" +
                        "매칭된 정보가 도착했습니다:\n" +
                        "이름: %s\n" +
                        "성별: %s\n" +
                        "전화번호: %s\n" +
                        "선호 음식 카테고리: %s",
                name, gender, phone, categories
        );
    }

    private String getMatchedCategories(MatchForm matchForm) {
        HashMap<String, ArrayList<String>> categoryMap = nyumtolicService.getCategoryMap();
        ArrayList<String> categories = categoryMap.getOrDefault(matchForm.getFoodType(), new ArrayList<>());
        Collections.shuffle(categories);

        // 리스트 크기를 고려하여 무작위로 3개의 요소를 선택합니다.
        List<String> selectedCategories = new ArrayList<>();
        for (int i = 0; i < 3 && i < categories.size(); i++) {
            selectedCategories.add(categories.get(i));
        }

        return String.join(", ", selectedCategories);
    }
}
