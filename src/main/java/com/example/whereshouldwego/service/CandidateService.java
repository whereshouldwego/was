package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.CandidateMessage;
import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.domain.Vote;
import com.example.whereshouldwego.dto.request.CandidateMessageRequestDto;
import com.example.whereshouldwego.dto.response.CandidateMessageResponseDto;
import com.example.whereshouldwego.dto.response.PlaceResponse;
import com.example.whereshouldwego.repository.postgres.CandidateRepository;
import com.example.whereshouldwego.repository.postgres.PlaceRepository;
import com.example.whereshouldwego.repository.postgres.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final SimpMessagingTemplate messagingTemplate;
    private final CandidateRepository candidateRepository;
    private final PlaceRepository placeRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public void handleIncomingCandidate(CandidateMessageRequestDto dto, String roomCode) {
        validate(dto, roomCode);

        Long placeId = dto.getPlaceId();
        Long userId = dto.getUserId();

        switch (dto.getActionType()) {
            case ADD_PLACE -> addPlace(roomCode, placeId);
            case REMOVE_PLACE -> removePlace(roomCode, placeId);
            case ADD_VOTE -> addVote(roomCode, userId, placeId);
            case REMOVE_VOTE -> removeVote(roomCode, userId, placeId);
            default -> throw new IllegalArgumentException("Unknown actionType: " + dto.getActionType());
        }

        CandidateMessageResponseDto response = buildResponse(roomCode, placeId);

        messagingTemplate.convertAndSend("/topic/candidate." + roomCode, response);
    }

    private void validate(CandidateMessageRequestDto dto, String roomCode) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new IllegalArgumentException("roomCode is required");
        }
        if (dto.getPlaceId() == null) {
            throw new IllegalArgumentException("placeId is required");
        }
        if (dto.getActionType() == null) {
            throw new IllegalArgumentException("actionType is required");
        }
        // ADD/REMOVE_VOTE 에서는 userId 필요
        switch (dto.getActionType()) {
            case ADD_VOTE, REMOVE_VOTE -> {
                if (dto.getUserId() == null) {
                    throw new IllegalArgumentException("userId is required for vote actions");
                }
            }
            default -> { }
        }
    }

    private CandidateMessageResponseDto buildResponse(String roomCode, Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("place not found: " + placeId));
        PlaceResponse placeResponse = PlaceResponse.from(place);

        List<Long> votedUserIds = voteRepository.findVotedUserIds(roomCode, placeId);
        int voteCount = votedUserIds.size();

        return CandidateMessageResponseDto.builder()
                .roomCode(roomCode)
                .place(placeResponse)
                .votedUserIds(votedUserIds)
                .voteCount(voteCount)
                .build();
    }

    private void addPlace(String roomCode, Long placeId) {
        if (!candidateRepository.existsByRoomCodeAndPlaceId(roomCode, placeId)) {
            candidateRepository.save(CandidateMessage.builder()
                    .roomCode(roomCode)
                    .placeId(placeId)
                    .build());
        }
    }

    private void removePlace(String roomCode, Long placeId) {
        candidateRepository.deleteByRoomCodeAndPlaceId(roomCode, placeId);
        voteRepository.deleteByRoomCodeAndPlaceId(roomCode, placeId);
    }

    private void addVote(String roomCode, Long userId, Long placeId) {
        if (!voteRepository.existsByRoomCodeAndUserIdAndPlaceId(roomCode, userId, placeId)) {
            voteRepository.save(Vote.builder()
                    .roomCode(roomCode)
                    .userId(userId)
                    .placeId(placeId)
                    .build());
        }
    }

    private void removeVote(String roomCode, Long userId, Long placeId) {
        voteRepository.deleteByRoomCodeAndUserIdAndPlaceId(roomCode, userId, placeId);
    }
}
