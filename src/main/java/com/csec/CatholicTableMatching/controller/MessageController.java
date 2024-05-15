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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final SendMessageService sendMessageService;
    private final EncryptService encryptService;
    private final MatchRepository matchRepository;



    @PostMapping("/send")
    public void sendMessage(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String text) {
        sendMessageService.sendMessage(from, to, text);
    }

    @PostMapping("/sendSms/{matchId}")
    public String sendSms(@PathVariable Long matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match not found"));
        if(!match.isSended()){
            sendUserDetailsToEachOther(match);
        }
        sendUserDetailsToEachOther(match);
        match.setSended(true);
        matchRepository.save(match);
        return "redirect:/matchResult";
    }

    @PostMapping("/sendAllSms")
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
        String user1Phone = match.getUser1().getPhoneNum();
        String user2Phone = match.getUser2().getPhoneNum();

        String user1Message = String.format("매칭된 유저 정보: \n이름: %s\n성별: %s\n전화번호: %s",
                match.getUser2().getName(), match.getUser2().getGender(), match.getUser2().getPhoneNum());

        String user2Message = String.format("매칭된 유저 정보: \n이름: %s\n성별: %s\n전화번호: %s",
                match.getUser1().getName(), match.getUser1().getGender(), match.getUser1().getPhoneNum());

        sendMessageService.sendMessage("01039077292", user1Phone, user1Message);
        sendMessageService.sendMessage("01039077292", user2Phone, user2Message);
    }


}
