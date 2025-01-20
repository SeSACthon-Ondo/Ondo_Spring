package com.ondo.ondo_back.recommendation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondo.ondo_back.common.dto.ReviewResponseDto;
import com.ondo.ondo_back.common.entity.Restaurants;
import com.ondo.ondo_back.common.entity.Review;
import com.ondo.ondo_back.common.repository.RestaurantsRepository;
import com.ondo.ondo_back.common.repository.ReviewRepository;
import com.ondo.ondo_back.recommendation.dto.RestaurantsRecommendRequestDto;
import com.ondo.ondo_back.recommendation.dto.RestaurantsRecommendResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RestaurantsRecommendService {

    private final ReviewRepository reviewRepository;
    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private final RestaurantsRepository restaurantsRepository;

    public RestaurantsRecommendService(RestaurantsRepository restaurantsRepository, ReviewRepository reviewRepository) {

        this.restaurantsRepository = restaurantsRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<RestaurantsRecommendResponseDto> getRecommendations(RestaurantsRecommendRequestDto restaurantsRecommendRequestDto) {

        String district = restaurantsRecommendRequestDto.getDistrict();
        double latitude = restaurantsRecommendRequestDto.getLatitude();
        double longitude = restaurantsRecommendRequestDto.getLongitude();
        String foodsQuery = restaurantsRecommendRequestDto.getFoodsQuery();

        // 반경 500m 내 음식점 조회
        List<Restaurants> nearbyRestaurants = restaurantsRepository.findbyNearbyRestaurants(latitude, longitude);
        List<String> storeCandidates = nearbyRestaurants.stream()
                .map(store -> String.format("%s, %s, %s", store.getName(), store.getAddress(), store.getCategory()))
                .collect(Collectors.toList());

        // OpenAI 프롬프트
        String prompt = buildPrompt(district, foodsQuery, storeCandidates);

        // OpenAI 호출
        String apiResponse = callOpenAI(prompt);

        // 응답 파싱
        List<Integer> recommendedIds = parseResponse(apiResponse);

        return fetchRestaurantsDetailsWithReviews(recommendedIds);
    }

    private String buildPrompt(String district, String foodsQuery, List<String> storeCandidates) {

        return String.format("""
                당신은 유저가 원하는 음식 %s에 대한 정보를 받고 그 정보를 기반으로 %s를 기반하여 어느 가맹점에서 해당 음식을 구매하거나 먹을 수 있는 장소를 찾아주는 챗봇의 역할을 수행합니다.
                유저의 현재 위치(위도, 경도)는 %s이고 유저를 중심으로 주변에 있는 가맹점 리스트로 %s가 있습니다.
                유저의 요구사항(%s)은 음식일수도 있고, 음식점일수도 있고, 요구사항 일수도 있습니다.
                유저가 원하는 음식 %s을 구매할 수 있는 장소 또는 해당 음식을 먹을 수 있는 장소를 %s 중에서 3곳을 추천해주세요.
                유저가 원하는 것(%s)와 가장 관련이 높은 가맹점을 분류를 기반으로 찾아주시면 됩니다.
                각각의 가맹점에 대해 %s에서 제공했던 (restaurantId)를 제공해주시면 됩니다.
                음식점 중 술과 관련된 음식점 또는 유흥업소는 제외합니다.
                
                이러한 정보를 제외하고는 어떠한 말도 포함되어 있어서는 안됩니다.
                출력의 형식은 아래의 예시와 같이 출력하면 됩니다. 반드시 준수해야 합니다.
                다른 형식이 아닌 아래 예시의 형태로 반드시 출력해야 합니다.
                예시)
                [2, 14, 5]
                """,
                foodsQuery,
                storeCandidates,
                district,
                storeCandidates,
                foodsQuery,
                foodsQuery,
                storeCandidates,
                foodsQuery,
                storeCandidates
        );
    }

    private String callOpenAI(String prompt) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // OpenAI API 요청 본문 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt))); // `messages` 배열 형식
            requestBody.put("max_tokens", 300);
            requestBody.put("temperature", 0.9);

            // JSON 문자열 변환
            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // HTTP 요청 헤더 설정
            org.springframework.http.HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, entity, String.class);
            System.out.println("🔹 OpenAI API 응답: " + response.getBody());

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("OpenAI API 호출 실패: " + response.getBody());
            }

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("OpenAI 요청 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private List<Integer> parseResponse(String apiResponse) {

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(apiResponse);

            String jsonString = rootNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText()
                    .trim();

            return objectMapper.readValue(jsonString, objectMapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
        } catch (Exception e) {

            throw new RuntimeException("OpenAI 응답 파싱 실패: " + e.getMessage(), e);
        }
    }

    private List<RestaurantsRecommendResponseDto> fetchRestaurantsDetailsWithReviews(List<Integer> recommendedIds) {

        List<Restaurants> recommendedRestaurants = restaurantsRepository.findAllById(recommendedIds);

        return recommendedRestaurants.stream()
                .map(restaurant -> {
                    List<Review> reviews = reviewRepository.findByTargetTypeAndTargetId(0, restaurant.getRestaurantId());
                    List<ReviewResponseDto> reviewResponseDtos = reviews.stream()
                            .map(this::mapToReviewDto)
                            .collect(Collectors.toList());

                    return RestaurantsRecommendResponseDto.builder()
                            .name(restaurant.getName())
                            .category(restaurant.getCategory())
                            .address(restaurant.getAddress())
                            .menu(restaurant.getMenu())
                            .reviews(reviewResponseDtos)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private ReviewResponseDto mapToReviewDto(Review review) {

        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .memberId(review.getMemberId())
                .content(review.getContent())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt().toString())
                .updatedAt(review.getUpdatedAt().toString())
                .build();
    }
}
