package com.csec.CatholicTableMatching.security.oauth;


import com.csec.CatholicTableMatching.security.domain.User;
import com.csec.CatholicTableMatching.security.domain.UserRole;
import com.csec.CatholicTableMatching.security.repository.CUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final CUserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;

        String provider = userRequest.getClientRegistration().getRegistrationId(); // 소셜 로그인 주체

        if(provider.equals("kakao")) {
            log.info("Login With Kakao");
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }

        String providerId = oAuth2UserInfo.getProviderId(); // 소셜로그인 서비스에서의 식별자
        String loginId = provider + "_" + providerId; // 중복방지용 회원 아이디

        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
        User user = null;

        // 계정이 없다면 회원가입
        if(optionalUser.isEmpty()) {
            user = User.builder()
                    .loginId(loginId)
                    .provider(provider)
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
        } else {
            // 있다면 조회
            user = optionalUser.get();
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}
