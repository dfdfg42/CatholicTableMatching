package com.csec.CatholicTableMatching.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
class NyumtolicServiceTest {

    // 주체
    NyumtolicService nyumtolicService = new NyumtolicService();

    // 협력자

    @Test
    @DisplayName("냠톨릭 API 음식점 목록 가져오기")
    void getRestaurant() {
        // given

        // when
        Mono<NyumtolicService.ResponseDTO> data = nyumtolicService.getRestaurants(500);

        // then
        System.out.println(data.block());
    }

    @Test
    @DisplayName("냠톨릭 API 음식점 카테고리 맵 가져오기")
    void getCategoryMap() {
        // given

        // when
        HashMap<String, ArrayList<String>> map = nyumtolicService.getCategoryMap();

        // then
        System.out.println(map);
    }

}