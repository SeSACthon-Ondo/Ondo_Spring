package com.ondo.ondo_back.recommendation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondo.ondo_back.common.entity.OfflineCultural;
import com.ondo.ondo_back.common.repository.OfflineCulturalRepository;
import com.ondo.ondo_back.recommendation.dto.CulturalRecommendRequestDto;
import com.ondo.ondo_back.recommendation.dto.CulturalRecommendResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CulturalRecommendService {

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    private OfflineCulturalRepository offlineCulturalRepository;

    public CulturalRecommendService(OfflineCulturalRepository offlineCulturalRepository) {

        this.offlineCulturalRepository = offlineCulturalRepository;
    }

    public List<CulturalRecommendResponseDto> getRecommendations(CulturalRecommendRequestDto culturalRecommendRequestDto) {

        String userLocation = culturalRecommendRequestDto.getUserLocation();
        String productQuery = culturalRecommendRequestDto.getProductQuery();
        String district = extractDistrict(userLocation);

        List<OfflineCultural> offlineCulturals = offlineCulturalRepository.findByAddressContaining(district);

        List<String> storeCandidates = offlineCulturals.stream()
                .map(store -> String.format("%s %s %s", store.getName(), store.getAddress(), store.getCategory()))
                .limit(15)
                .collect(Collectors.toList());

        // OpenAI 프롬프트
        String prompt = buildPrompt(userLocation, productQuery, storeCandidates);

        // OpenAI 호출
        String apiResponse = callOpenAI(prompt);

        // 응답 파싱 및 반환
        return parseResponse(apiResponse);
    }

    private String extractDistrict(String userLocation) {

        if (userLocation == null || !userLocation.contains("구")) {

            throw new IllegalArgumentException("유효하지 않은 지역 형식입니다.: " + userLocation);
        }

        return userLocation.split("구")[0] + "구";
    }

    private String buildPrompt(String userLocation, String productQuery, List<String> storeCandidates) {

        return String.format("""
                당신은 유저가 원하는 물품 %s에 대한 정보를 받고 그 정보를 기반으로 %s를 기반하여 어느 가맹점에서 해당 물품을 구매하거나 해당 행위를 할 수 있는 장소를 찾아주는 챗봇의 역할을 수행합니다.
                유저의 현재 위치(위도, 경도)는 %s이고 유저를 중심으로 주변에 있는 가맹점 리스트로 %s가 있습니다.
                유저가 원하는 것(%s을 구매할 수 있는 장소 또는 해당 행위를 할 수 있는 장소를 %s 중에서 3곳을 추천해주세요.
                분류는 공연, 체육용품, 영상, 문화체험, 도서, 미술, 여행사, 교통수단, 숙박, 음악, 관광지, 체육시설, 스포츠관람 로 나누어집니다.
                유저가 원하는 것(%s)와 가장 관련이 높은 가맹점을 위의 분류를 기반으로 찾아주시면 됩니다.
                예를 들면, 책(독서)는 도서, 영화는 공연 또는 영상, 자전거는 체육용품, 미술용품은 미술, 여행상품은 여행사, 숙박은 숙박 등으로 분류됩니다.
                각각의 가맹점에 대해 가맹점 이름, 가맹점 분류, 가맹점 위치(주소)를 제공해주시면 됩니다.
                음식점 중 술과 관련된 음식점 또는 유흥업소는 제외합니다.
                
                이러한 정보를 제외하고는 어떠한 말도 포함되어 있어서는 안됩니다.
                출력의 형식은 아래의 예시와 같이 출력하면 됩니다. 반드시 준수해야 합니다.
                다른 형식이 아닌 아래 예시의 형태로 반드시 출력해야 합니다.
                예시)
                [
                   {{
                      가맹점 이름: 한국마술문화협회,
                      가맹점 카테고리: 공연,
                      가맹점 위치: 서울시 강남구 역삼동 123-45,
                   }},
                   {{
                      가맹점 이름: 삼천리자전거신월2동점,
                      가맹점 카테고리: 체육용품,
                      가맹점 위치: 서울 양천구 오목로 61,
                   }},
                   {{
                      가맹점 이름: CGV 미아,
                      가맹점 카테고리: 영상,
                      가맹점 위치: 서울 강북구 도봉로 34트레지오 쇼핑몰 9층 일부 1 (미아동),
                   }}
                ]
                
                """,
                productQuery,
                storeCandidates,
                userLocation,
                storeCandidates,
                productQuery,
                storeCandidates,
                productQuery
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
            HttpHeaders headers = new HttpHeaders();
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

    private List<CulturalRecommendResponseDto> parseResponse(String apiResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(apiResponse);

            // 🔹 OpenAI 응답 확인
            System.out.println("🔹 OpenAI 원본 응답:\n" + rootNode.toPrettyString());

            if (!rootNode.has("choices") || rootNode.get("choices").isEmpty()) {
                throw new RuntimeException("OpenAI 응답에서 'choices'가 존재하지 않음.");
            }

            // 🔹 OpenAI 응답에서 JSON 문자열을 추출
            String jsonString = rootNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText()
                    .trim();

            System.out.println("🔹 OpenAI 응답에서 추출된 JSON 문자열:\n" + jsonString);

            // 🔹 JSON 문자열을 정제
            String cleanedJson = cleanAndFormatJson(jsonString);
            System.out.println("🔹 정제된 JSON 문자열:\n" + cleanedJson);

            // 🔹 JSON 문자열을 실제 JSON 객체로 변환
            JsonNode recommendationsNode = objectMapper.readTree(cleanedJson);

            return objectMapper.convertValue(
                    recommendationsNode,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CulturalRecommendResponseDto.class)
            );
        } catch (Exception e) {
            throw new RuntimeException("OpenAI 응답을 파싱하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    private String cleanAndFormatJson(String rawJson) {
        return rawJson
                .replace("{{", "{")  // 중괄호 정리
                .replace("}}", "}")
                .replace("가맹점 이름:", "\"name\":")  // 키 이름 변경
                .replace("가맹점 카테고리:", "\"category\":")
                .replace("가맹점 위치:", "\"address\":")
                .replaceAll("(?<=:\\s)([가-힣0-9a-zA-Z\\s()]+)", "\"$1\"")  // 값 감싸기
                .replaceAll("(?<=\")\\n", "")  // 개행 문자 제거
                .replaceAll(",\\s*}", "}");  // **마지막 쉼표 제거**
    }
}
