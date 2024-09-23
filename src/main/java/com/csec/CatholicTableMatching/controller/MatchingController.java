package com.csec.CatholicTableMatching.controller;

import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.domain.MatchForm;
import com.csec.CatholicTableMatching.repository.MatchFormRepository;
import com.csec.CatholicTableMatching.security.domain.User;
import com.csec.CatholicTableMatching.security.oauth.PrincipalDetails;
import com.csec.CatholicTableMatching.security.repository.UserRepository;
import com.csec.CatholicTableMatching.security.service.EncryptService;
import com.csec.CatholicTableMatching.service.MatchingService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    private final UserRepository userRepository;
    private final EncryptService encryptService;

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/match")
    @PreAuthorize("isAuthenticated()")
    public String matchStart(@AuthenticationPrincipal PrincipalDetails userDetails, Model model) {
        User loginUser = userRepository.findByLoginId(userDetails.getUsername())
                .orElseThrow(RuntimeException::new);

        if (loginUser.getPhoneNum() == null) {
            return "redirect:/userinfo";
        } else if (loginUser.getMatchForm() != null) {
            return "redirect:/matching";
        } else {
            loginUser.setPhoneNum(encryptService.decrypt(loginUser.getPhoneNum()));
            model.addAttribute("user", loginUser);
            model.addAttribute("matchForm", new MatchForm());
            return "match_form_nes";
        }
    }

    @PostMapping("/match")
    @PreAuthorize("isAuthenticated()")
    public String findMatches(@ModelAttribute("matchForm") MatchForm matchForm, Principal principal) {
        String loginId = principal.getName();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        matchForm.setUser(user);
        user.setMatchForm(matchForm);
        userRepository.save(user);

        return "redirect:/matching";
    }

    @GetMapping("/matching")
    @PreAuthorize("isAuthenticated()")
    public String matching(@AuthenticationPrincipal PrincipalDetails userDetails, Model model) {
        User loginUser = userRepository.findByLoginId(userDetails.getUsername())
                .orElseThrow(RuntimeException::new);

        if (loginUser.isMatched()) {
            return "redirect:/match/" + loginUser.getId();
        }
        model.addAttribute("user", loginUser);
        return "matching";
    }

    @PostMapping("/matchResult")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createAllMatches(@AuthenticationPrincipal PrincipalDetails userDetails, Model model) {
        matchingService.createMatchForAllUsers();
        User loginUser = userRepository.findByLoginId(userDetails.getUsername())
                .orElseThrow(RuntimeException::new);
        List<Match> matches = matchingService.MatchResult();
        model.addAttribute("user", loginUser);
        model.addAttribute("matches", matches);
        return "redirect:/matchResult";
    }

    @GetMapping("/matchResult")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String findAllMatches(@AuthenticationPrincipal PrincipalDetails userDetails, Model model) {
        User loginUser = userRepository.findByLoginId(userDetails.getUsername())
                .orElseThrow(RuntimeException::new);
        List<Match> matches = matchingService.MatchResult();
        model.addAttribute("user", loginUser);
        model.addAttribute("matches", matches);
        return "match_results";
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping("/match/{userId}")
    public String matchStatus(@AuthenticationPrincipal PrincipalDetails userDetails,
                              @PathVariable("userId") Long userId, Model model) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("No customer found with ID " + userId));
        if (!user.getLoginId().equals(userDetails.getUsername())) {
            return "redirect:/match";
        }
        if (!user.isMatched() && user.getMatchForm() != null) {
            return "redirect:/matching";
        }
        if (user.isMatched()) {
            User partner = matchingService.findPartner(user);
            String partnerName = partner.getName();
            model.addAttribute("partnerName", partnerName);
        }
        model.addAttribute("user", user);
        return "match_status";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/userinfo")
    public String userInfo(@AuthenticationPrincipal PrincipalDetails userDetails, Model model) {
        User loginUser = userRepository.findByLoginId(userDetails.getUsername())
                .orElseThrow(RuntimeException::new);
        if (loginUser.getPhoneNum() != null) {
            loginUser.setPhoneNum(encryptService.decrypt(loginUser.getPhoneNum()));
        }
        model.addAttribute("user", loginUser);
        return "user_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/userinfo")
    public String saveUserInfo(@AuthenticationPrincipal PrincipalDetails userDetails,
                               @ModelAttribute("user") User user) {
        String username = userDetails.getUsername();
        User loginUser = userRepository.findByLoginId(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        loginUser.updateUserInfo(user.getName(), user.getGender(), encryptService.encrypt(user.getPhoneNum()));
        userRepository.save(loginUser);

        return "redirect:/match";
    }
   /* @PostConstruct
    @Transactional
    public void testCreateMatch() {
        String phone1 = "01039077292";
        String phone2 = "01094069318";

        // User 객체 생성
        User userko = new User("고경우", "123", "M", encryptService.encrypt(phone1), false);
        User userkim = new User("이승원", "145", "F", encryptService.encrypt(phone2), false);

        // MatchForm 생성 (다중 시간대)
        List<Integer> timeSlotsKo = Arrays.asList(18, 19, 20); // 예: 저녁시간대 (18시, 19시, 20시)
        List<Integer> timeSlotsKim = Arrays.asList(18, 19, 20); // 예: 저녁시간대

        MatchForm matchFormko = new MatchForm(userko, "한식", timeSlotsKo, "이성");
        MatchForm matchFormkim = new MatchForm(userkim, "한식", timeSlotsKim, "이성");

        // User와 MatchForm 연결
        userko.setMatchForm(matchFormko);
        userkim.setMatchForm(matchFormkim);

        // 저장
        userRepository.save(userko);
        userRepository.save(userkim);


// 추가할 테스트 더미 데이터

// 1. 남남 동성
        String phone3 = "01012345678";
        String phone4 = "01087654321";

        User userA = new User("김철수", "200", "M", encryptService.encrypt(phone3), false);
        User userB = new User("박영희", "201", "M", encryptService.encrypt(phone4), false);

        List<Integer> timeSlotsA = Arrays.asList(9, 10, 11); // 예: 오전시간대
        List<Integer> timeSlotsB = Arrays.asList(9, 10, 11); // 예: 오전시간대

        MatchForm matchFormA = new MatchForm(userA, "중식", timeSlotsA, "동성");
        MatchForm matchFormB = new MatchForm(userB, "중식", timeSlotsB, "동성");

        userA.setMatchForm(matchFormA);
        userB.setMatchForm(matchFormB);

        userRepository.save(userA);
        userRepository.save(userB);

// 2. 남녀 동성
        String phone5 = "01011223344";
        String phone6 = "01044332211";

        User userC = new User("최민수", "300", "M", encryptService.encrypt(phone5), false);
        User userD = new User("이수진", "301", "F", encryptService.encrypt(phone6), false);

        List<Integer> timeSlotsC = Arrays.asList(14, 15, 16); // 예: 오후시간대
        List<Integer> timeSlotsD = Arrays.asList(14, 15, 16); // 예: 오후시간대

        MatchForm matchFormC = new MatchForm(userC, "일식", timeSlotsC, "동성");
        MatchForm matchFormD = new MatchForm(userD, "일식", timeSlotsD, "동성");

        userC.setMatchForm(matchFormC);
        userD.setMatchForm(matchFormD);

        userRepository.save(userC);
        userRepository.save(userD);

// 3. 남녀 이성
        String phone7 = "01055667788";
        String phone8 = "01088776655";

        User userE = new User("장혁", "400", "M", encryptService.encrypt(phone7), false);
        User userF = new User("홍길동", "401", "F", encryptService.encrypt(phone8), false);

        List<Integer> timeSlotsE = Arrays.asList(19, 20, 21); // 예: 저녁시간대
        List<Integer> timeSlotsF = Arrays.asList(19, 20, 21); // 예: 저녁시간대

        MatchForm matchFormE = new MatchForm(userE, "양식", timeSlotsE, "이성");
        MatchForm matchFormF = new MatchForm(userF, "양식", timeSlotsF, "이성");

        userE.setMatchForm(matchFormE);
        userF.setMatchForm(matchFormF);

        userRepository.save(userE);
        userRepository.save(userF);

        // 무작위 생성기
        Random random = new Random();
        List<String> foodTypes = Arrays.asList("한식", "일식", "양식", "중식");
        List<String> genderPreferences = Arrays.asList("동성", "이성"); // 동성 또는 이성 중 선택

        // 가능한 시간대 (0~23 시간대, 랜덤 선택용)
        List<Integer> possibleTimeSlots = Arrays.asList(9, 10, 12, 18, 19, 20);

        // 200명의 유저 생성
        IntStream.rangeClosed(1, 200).forEach(i -> {
            String userName = "user" + i;
            String gender = i % 2 == 0 ? "F" : "M"; // 짝수는 여성, 홀수는 남성
            String phoneNum = "010" + String.format("%08d", i); // 고유한 전화번호 생성
            boolean matched = false;

            // User 객체 생성 (전화번호 암호화)
            User user = new User(userName, userName, gender, encryptService.encrypt(phoneNum), matched);

            // 랜덤으로 foodType 선택
            String foodType = foodTypes.get(random.nextInt(foodTypes.size()));

            // 랜덤으로 genderPreference 선택 (동성 또는 이성)
            String preferGender = genderPreferences.get(random.nextInt(genderPreferences.size()));

            // 랜덤으로 1~3개의 시간대 선택
            Collections.shuffle(possibleTimeSlots);
            List<Integer> randomTimeSlots = new ArrayList<>();
            int numberOfSlots = random.nextInt(3) + 1; // 1~3개의 시간대 선택
            for (int j = 0; j < numberOfSlots; j++) {
                randomTimeSlots.add(possibleTimeSlots.get(j));
            }

            // MatchForm 객체 생성
            MatchForm matchForm = new MatchForm(user, foodType, randomTimeSlots, preferGender);

            // User와 MatchForm 연결
            user.setMatchForm(matchForm);

            // User 저장
            userRepository.save(user);
        });

        System.out.println("테스트용 매칭 데이터가 생성되었습니다.");
    }*/

}





