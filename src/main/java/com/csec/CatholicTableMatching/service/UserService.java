package com.csec.CatholicTableMatching.service;

import com.csec.CatholicTableMatching.domain.User;
import com.csec.CatholicTableMatching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;



    public User findUserById(Long userId) {
        // findById 메서드는 Optional 객체를 반환합니다.
        Optional<User> customer = userRepository.findById(userId);

        // 사용자가 존재하지 않을 경우에 대한 처리
        // 여기서는 예외를 던지거나, 적절한 처리를 할 수 있습니다.
        return customer.orElseThrow(() -> new RuntimeException("No customer found with ID " + userId));
    }



}
