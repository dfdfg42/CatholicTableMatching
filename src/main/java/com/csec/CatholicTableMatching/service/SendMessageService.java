package com.csec.CatholicTableMatching.service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.service.DefaultMessageService;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class SendMessageService {

    private static final Logger logger = Logger.getLogger(SendMessageService.class.getName());

    private final DefaultMessageService messageService;

    public SendMessageService(
            @Value("${coolsms.apiKey}") String apiKey,
            @Value("${coolsms.apiSecret}") String apiSecret,
            @Value("${coolsms.apiUrl}") String apiUrl) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, apiUrl);
    }

    public void sendMessage(String from, String to, String text) {
        try {
            Message message = new Message();
            message.setFrom(from); // 01012345678 형태여야 함.
            message.setTo(to); // 01012345678 형태여야 함.
            message.setText(text);

            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println(response);
            logger.info("Message sent successfully: " + response);
        } catch (Exception e) {
            logger.severe("Failed to send message: " + e.getMessage());
        }
    }
}
