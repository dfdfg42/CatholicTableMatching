//package com.csec.CatholicTableMatching.service;
//
//import net.nurigo.sdk.message.response.SingleMessageSentResponse;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class SendMessageServiceTest {
//
//    @Autowired
//    private SendMessageService sendMessageService;
//
//    @Test
//    public void testSendMessage() {
//        String from = "01039077292"; // 테스트용 발신자 번호
//        String to = "01039077292"; // 테스트용 수신자 번호
//        String text = "테스트 메시지 내용";
//
//        // 메시지 전송
//        sendMessageService.sendMessage(from, to, text);
//
//        // 메시지 전송 후 결과를 확인하는 로그 출력
//        System.out.println("메시지 전송 테스트 완료.");
//    }
//}
