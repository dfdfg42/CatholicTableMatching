package com.csec.CatholicTableMatching.controller;

import com.csec.CatholicTableMatching.service.SendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    private final SendMessageService sendMessageService;

    @Autowired
    public MessageController(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @PostMapping("/send")
    public void sendMessage(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String text) {
        sendMessageService.sendMessage(from, to, text);
    }
}
