package com.csec.CatholicTableMatching.controller;

import com.csec.CatholicTableMatching.service.SendMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final SendMessageService messageService;

    @GetMapping("/sendSms/{matchId}")
    public String sendSms(@PathVariable Long matchId) {
        messageService.sendSms(matchId);
        return "redirect:/matchResult";
    }

    @GetMapping("/sendAllSms")
    public String sendAllSms() {
        messageService.sendAllSms();
        return "redirect:/matchResult";
    }
}
