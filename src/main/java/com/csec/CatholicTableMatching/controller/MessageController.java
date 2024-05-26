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
        String matchedCategories = getMatchedCategories(match.getUser1().getMatchForm());

        String user1Message = formatMessage(
                match.getUser2().getName(),
                user2Phone,
                matchedCategories,
                match.getTimeSlot()
        );

        String user2Message = formatMessage(
                match.getUser1().getName(),
                user1Phone,
                matchedCategories,
                match.getTimeSlot()
        );

        sendMessageService.sendMessage("01039077292", user1Phone, user1Message);
        sendMessageService.sendMessage("01039077292", user2Phone, user2Message);
    }

    private String formatMessage(String name,  String phone, String categories, String timeslot) {
        return String.format(
                "[만냠-맛있는 만냠]\n\n" +
                        "만냠 정보가 도착했습니다!\n\n" +
                        "같이 밥먹을 사람: %s\n" +
                        "전화번호: %s\n" +
                        "냠톨릭에서 뽑은 랜덤 음식점 3개: \n %s\n" +
                        "만날 시간대: %s\n" +
                        "다른 음식점에서 만나고싶다면 냠톨릭에서 목록 확인 \n https://nyumtolic.com",
                name, phone, categories,timeslot
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
