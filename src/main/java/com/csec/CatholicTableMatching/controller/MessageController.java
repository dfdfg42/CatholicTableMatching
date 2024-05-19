package com.csec.CatholicTableMatching.controller;

import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.repository.MatchFormRepository;
import com.csec.CatholicTableMatching.repository.MatchRepository;
import com.csec.CatholicTableMatching.security.repository.UserRepository;
import com.csec.CatholicTableMatching.security.service.EncryptService;
import com.csec.CatholicTableMatching.service.MatchingService;
import com.csec.CatholicTableMatching.service.SendMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final SendMessageService sendMessageService;
    private final EncryptService encryptService;
    private final MatchRepository matchRepository;


    @GetMapping("/send") // 어드민용
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String sendMessage(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String text) {
        sendMessageService.sendMessage(from, to, text);
        return "redirect:/matchResult";
    }

    @GetMapping("/sendSms/{matchId}")
    public String sendSms(@PathVariable Long matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match not found"));
        if(!match.isSended()){
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
            if(!match.isSended()){
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

        String user1Message = String.format("만냠-같이 밥먹을 사람 구하기\n 매칭된 정보가 도착했습니다: \n이름: %s\n성별: %s\n전화번호: %s",
                match.getUser2().getName(), match.getUser2().getGender(), user2Phone);

        String user2Message = String.format("만냠-같이 밥먹을 사람 구하기\n 매칭된 정보가 도착했습니다: \n이름: %s\n성별: %s\n전화번호: %s",
                match.getUser1().getName(), match.getUser1().getGender(), user1Phone);

        sendMessageService.sendMessage("01039077292", user1Phone, user1Message);
        sendMessageService.sendMessage("01039077292", user2Phone, user2Message);
    }


}
