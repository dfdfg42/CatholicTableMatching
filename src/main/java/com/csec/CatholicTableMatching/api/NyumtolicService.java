package com.csec.CatholicTableMatching.api;

import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class NyumtolicService {

    private final WebClient client = WebClient.create("https://nyumtolic.com/api");

    public HashMap<String, ArrayList<String>> getCategoryMap() {
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        List<RestaurantDTO> restaurants = getRestaurants(500)
                .block()
                .getContent();

        for (RestaurantDTO restaurant : restaurants) {
            List<CategoryDTO> categorys = restaurant.getCategories();
            for (CategoryDTO category : categorys) {
                ArrayList<String> list = map.getOrDefault(category.getName(), new ArrayList<>());
                list.add(restaurant.getName());
                map.put(category.getName(), list);
            }
        }

        return map;
    }

    public Mono<ResponseDTO> getRestaurants(int size) {
        String uri = "/restaurants?size=" + size;
        Mono<ResponseDTO> data = client
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ResponseDTO.class);
        return data;
    }

    @Data
    public static class ResponseDTO {
        private long currentPage;
        private long totalPages;
        private long totalElements;
        private List<RestaurantDTO> content;
    }

    @Data
    public static class RestaurantDTO {
        private Long id;
        private String photo;
        private String name;
        private String address;
        private String phoneNumber;
        private List<CategoryDTO> categories;
        private Double rating;
        private List<String> menu;
        private String description;
        private Integer travelTime;
        private Double latitude;
        private Double longitude;
        private Double userRating;
    }

    @Data
    public static class CategoryDTO {
        private Long id;
        private String name;
    }

}
