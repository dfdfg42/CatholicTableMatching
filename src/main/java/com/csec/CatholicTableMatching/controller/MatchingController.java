package com.csec.CatholicTableMatching.controller;

import com.csec.CatholicTableMatching.domain.Match;
import com.csec.CatholicTableMatching.domain.MatchForm;
import com.csec.CatholicTableMatching.repository.MatchFormRepository;
import com.csec.CatholicTableMatching.repository.MatchRepository;
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
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    private final UserRepository userRepository;

    private final MatchFormRepository matchFormRepository;

    private final EncryptService encryptService;

    private MatchForm matchForm;

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/match")
    @PreAuthorize("isAuthenticated()")
    public String MatchStart(@AuthenticationPrincipal PrincipalDetails userDetails,@ModelAttribute User user , Model model){
        User loginUser = userRepository.findByLoginId(userDetails.getUsername()).orElseThrow(
                () -> new RuntimeException());
         if (loginUser.getPhoneNum() == null) {
            return "redirect:/userinfo";
        }else if(loginUser.getMatchForm() != null){
             return "redirect:/matching";
        }
        else{
             model.addAttribute("matchForm", new MatchForm());
             return "match_form_nes";
         }
    } // 매치 폼 화면이동

    @PostMapping("/match")
    @PreAuthorize("isAuthenticated()")
    public String findMatches(@ModelAttribute("matchForm") MatchForm matchForm, Principal principal) {
        // 현재 인증된 사용자의 정보를 조회
        String loginId = principal.getName();
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // MatchForm과 User를 연결
        matchForm.setUser(user);
        user.setMatchForm(matchForm); // 편의 메서드를 사용하여 양방향 설정
        userRepository.save(user);    // User 저장

        return "redirect:/matching";  // 사용자의 ID를 이용해 리디렉션
    }

    @GetMapping("/matching")
    @PreAuthorize("isAuthenticated()")
    public String matching(){
        return "matching";
    } //사용자가 매칭을 넣었을때 넣었다고 보여지는 화면


    //전체 매칭한 결과 페이지
    @GetMapping("/matchResult")
/*    @PreAuthorize("isAuthenticated()")*/
    public String findAllMatches(Model model) {
        matchingService.createMatchForAllUsers();
        List<Match> matches = matchingService.MatchResult();
        model.addAttribute("matches", matches);
        System.out.println(matches);
        return "match_results";  // 매칭 결과 페이지 뷰 이름
    }

    @PreAuthorize("isAuthenticated()") // todo 사용자 id와 인증한 주체의 id가 동일한지도 검사 필요
    @RequestMapping("/match/{userId}")
    public String MatchStatus(@PathVariable("userId") Long userId, Model model){
        User user = userRepository.findUserById(userId).orElseThrow(
                () -> new RuntimeException("No customer found with ID " + userId));
        model.addAttribute("customer", user);
        return "match_status";

    } // 매치 현황판*/ 사용자가 나중에 매칭결과를 확인할수 있게 만드는창??

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/userinfo")
    public String userInfo(@AuthenticationPrincipal PrincipalDetails userDetails, @ModelAttribute User user){
        User loginUser = userRepository.findByLoginId(userDetails.getUsername()).orElseThrow(
                () -> new RuntimeException());


        if (loginUser.getPhoneNum() ==null){
            user.updateUserInfo(loginUser.getName(),loginUser.getGender(), loginUser.getPhoneNum() );
        }else{
        user.updateUserInfo(loginUser.getName(),loginUser.getGender(), encryptService.decrypt(loginUser.getPhoneNum()));
        }
        return "user_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/userinfo")
    public String saveUserInfo(@AuthenticationPrincipal PrincipalDetails userDetails, @ModelAttribute("user") User user) {
        String username = userDetails.getUsername();
        User loginUser = userRepository.findByLoginId(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        loginUser.updateUserInfo(user.getName(),user.getGender(), encryptService.encrypt(user.getPhoneNum()));

        userRepository.save(loginUser);


        return "redirect:/match";
    }


    @PostConstruct
    @Transactional
    public void testCreateMatch() {

        IntStream.rangeClosed(1, 100).forEach(i -> {
            String userName = "user" + i;
            String gender = i % 2 == 0 ? "F" : "M"; // 짝수는 여성, 홀수는 남성
            String phoneNum = "0101234123" + i;
            boolean matched = false;

            // User 객체 생성
            User user = new User(userName, userName, gender, phoneNum, matched);

            // MatchForm 객체 생성
            MatchForm matchForm = new MatchForm(user, "Italian", "Evening", "F");

            // User와 MatchForm 연결
            user.setMatchForm(matchForm);

            // User 저장
            userRepository.save(user);
        });
    }
}

