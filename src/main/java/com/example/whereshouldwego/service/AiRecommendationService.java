package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.ChatMessage;
import com.example.whereshouldwego.domain.Recommendation;
import com.example.whereshouldwego.dto.response.ChatMessageResponseDto;
import com.example.whereshouldwego.dto.response.RecommendedPlaceDetail;
import com.example.whereshouldwego.repository.mongo.ChatMessageRepository;
import com.example.whereshouldwego.repository.postgres.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiRecommendationService {

    private final WebClient webClient;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RecommendationRepository recommendationRepository;

    public void getRecommendationAsync(String userMessage, String roomCode) {
        webClient.post()
                .uri("http://localhost:8001/api/recommend")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("query", userMessage))
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(
                        // 성공 시 처리
                        apiResponse -> {
                            try {
                                String content = (String) apiResponse.get("content");
                                List<Map<String, Object>> places = (List<Map<String, Object>>) apiResponse.get("places");

                                // AI 응답 메시지 생성 및 저장
                                ChatMessage aiMessage = ChatMessage.builder()
                                        .userId(null)
                                        .username("AI")
                                        .roomCode(roomCode)
                                        .content(content)
                                        .createdAt(LocalDateTime.now())
                                        .isAiRequest(false)
                                        .build();

                                ChatMessage savedAiMessage = chatMessageRepository.save(aiMessage);

                                // 기본 응답 생성
                                ChatMessageResponseDto aiResponse = ChatMessageResponseDto.fromEntity(savedAiMessage);

                                // places 정보를 RecommendedPlaceDetail로 변환하여 추가
                                List<RecommendedPlaceDetail> placeDetails = convertToPlaceDetails(places);

                                // places 정보를 포함한 새로운 응답 생성
                                ChatMessageResponseDto responseWithPlaces = ChatMessageResponseDto.builder()
                                        .id(aiResponse.getId())
                                        .userId(aiResponse.getUserId())
                                        .username(aiResponse.getUsername())
                                        .roomCode(aiResponse.getRoomCode())
                                        .content(aiResponse.getContent())
                                        .createdAt(aiResponse.getCreatedAt())
                                        .isAiRequest(aiResponse.getIsAiRequest())
                                        .places(placeDetails) // places 정보 추가
                                        .build();

                                // 추천 결과를 Recommendations 테이블에 저장
                                saveRecommendations(roomCode, places);

                                // WebSocket으로 AI 응답 전송
                                messagingTemplate.convertAndSend("/topic/chat." + roomCode, responseWithPlaces);

                                log.info("AI 응답 전송 완료 - 룸코드: {}", roomCode);

                            } catch (Exception e) {
                                log.error("AI 응답 처리 중 오류 발생: ", e);
                                sendErrorMessage(roomCode);
                            }
                        },
                        // API 호출 에러 시 처리
                        error -> {
                            log.error("FastAPI 호출 중 오류 발생: ", error);
                            sendErrorMessage(roomCode);
                        }
                );
    }

    private List<RecommendedPlaceDetail> convertToPlaceDetails(List<Map<String, Object>> places) {
        if (places == null) {
            return List.of();
        }

        return places.stream()
                .map(place -> RecommendedPlaceDetail.builder()
                        .id(((Integer) place.get("id")).longValue())
                        .name((String) place.get("name"))
                        .category((String) place.get("category"))
                        .address((String) place.get("address"))
                        .roadAddress((String) place.get("road_address"))
                        .x(((Number) place.get("x")).doubleValue())
                        .y(((Number) place.get("y")).doubleValue())
                        .similarityScore(((Number) place.get("similarity_score")).doubleValue())
                        .finalScore(place.get("final_score") != null ?
                                ((Number) place.get("final_score")).doubleValue() : null)
                        .matchCount(place.get("match_count") != null ?
                                ((Integer) place.get("match_count")) : null)
                        .distanceKm(place.get("distance_km") != null ?
                                ((Number) place.get("distance_km")).doubleValue() : null)
                        .build())
                .collect(Collectors.toList());
    }

    private void saveRecommendations(String roomCode, List<Map<String, Object>> places) {
        try {
            if (places != null && !places.isEmpty()) {
                Long roomId = Long.parseLong(roomCode);

                for (Map<String, Object> place : places) {
                    Integer placeId = (Integer) place.get("id");

                    if (placeId != null) {
                        Recommendation recommendation = Recommendation.builder()
                                .roomId(roomId)
                                .placeId(placeId.longValue())
                                .build();

                        recommendationRepository.save(recommendation);
                    }
                }
                log.info("추천 결과 저장 완료 - 룸ID: {}, 장소 수: {}", roomId, places.size());
            }
        } catch (Exception e) {
            log.error("추천 결과 저장 중 오류 발생: ", e);
        }
    }

    private void sendErrorMessage(String roomCode) {
        try {
            ChatMessage errorMessage = ChatMessage.builder()
                    .userId(null)
                    .username("AI")
                    .roomCode(roomCode)
                    .content("죄송합니다. AI 응답 생성 중 오류가 발생했습니다.")
                    .createdAt(LocalDateTime.now())
                    .isAiRequest(false)
                    .build();

            ChatMessage savedErrorMessage = chatMessageRepository.save(errorMessage);
            ChatMessageResponseDto errorResponse = ChatMessageResponseDto.fromEntity(savedErrorMessage);

            messagingTemplate.convertAndSend("/topic/chat." + roomCode, errorResponse);

        } catch (Exception ex) {
            log.error("에러 메시지 전송 실패: ", ex);
        }
    }
}