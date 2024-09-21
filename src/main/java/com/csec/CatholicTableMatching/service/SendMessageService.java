package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.api.NyumtolicService;
import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.domain.MatchForm;
import com.csec.CatholicTableMatching.repository.MatchRepository;
import com.csec.CatholicTableMatching.security.service.EncryptService;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class SendMessageService {

    private static final Logger logger = Logger.getLogger(SendMessageService.class.getName());

    private final MatchRepository matchRepository;
    private final EncryptService encryptService;
    private final NyumtolicService nyumtolicService;
    private final DefaultMessageService messageService;
    //private final String senderPhoneNumber;

    @Autowired
    public SendMessageService(
            MatchRepository matchRepository,
            EncryptService encryptService,
            NyumtolicService nyumtolicService,
            @Value("${coolsms.apiKey}") String apiKey,
            @Value("${coolsms.apiSecret}") String apiSecret,
            @Value("${coolsms.apiUrl}") String apiUrl
            //@Value("${sender.phone.number}") String senderPhoneNumber
    ) {
        this.matchRepository = matchRepository;
        this.encryptService = encryptService;
        this.nyumtolicService = nyumtolicService;
        //this.senderPhoneNumber = senderPhoneNumber;
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, apiUrl);
    }
    public void sendSms(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        if (!match.isSended()) {
            sendUserDetailsToEachOther(match);
            match.setSended(true);
            matchRepository.save(match);
        }
    }

    public void sendAllSms() {
        List<Match> matches = matchRepository.findAll();

        for (Match match : matches) {
            if (!match.isSended()) {
                sendUserDetailsToEachOther(match);
                match.setSended(true);
                matchRepository.save(match);
            }
        }
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

        sendMessage(user1Phone, user1Message);
        sendMessage(user2Phone, user2Message);
    }

    private String formatMessage(String name, String phone, String categories, String timeslot) {
        return String.format(
                "[만냠-맛있는 만냠]\n\n" +
                        "만냠 정보가 도착했습니다!\n\n" +
                        "같이 밥먹을 사람: %s\n" +
                        "전화번호: %s\n" +
                        "냠톨릭에서 뽑은 랜덤 음식점 3개: \n%s\n" +
                        "만날 시간대: %s\n" +
                        "다른 음식점에서 만나고 싶다면 냠톨릭에서 목록 확인 \nhttps://nyumtolic.com",
                name, phone, categories, timeslot
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

    private void sendMessage(String to, String text) {
        try {
            Message message = new Message();
            message.setFrom("01039077292"); // 보내는 사람 번호
            message.setTo(to); // 받는 사람 번호
            message.setText(text);

            // 여기서 sendMessageService 대신 messageService를 사용합니다.
            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
            logger.info("Message sent successfully: " + response);
        } catch (Exception e) {
            logger.severe("Failed to send message: " + e.getMessage());
        }
    }
}