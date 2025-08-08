package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.CandidateMessage;
import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.domain.Vote;
import com.example.whereshouldwego.domain.type.CandidateActionType;
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

import java.util.ArrayList;
import java.util.List;

import static com.example.whereshouldwego.util.RoomCodeUtil.decode;
import static com.example.whereshouldwego.util.RoomCodeUtil.encode;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final SimpMessagingTemplate messagingTemplate;
    private final CandidateRepository candidateRepository;
    private final PlaceRepository placeRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public void handleIncomingCandidate(CandidateMessageRequestDto dto, String roomCode) {
        Long roomId = decode(roomCode);
        validate(dto, roomCode, roomId);

        Long placeId = dto.getPlaceId();
        Long userId = dto.getUserId();

        switch (dto.getActionType()) {
            case ADD_PLACE -> addPlace(roomId, placeId);
            case REMOVE_PLACE -> removePlace(roomId, placeId);
            case ADD_VOTE -> addVote(roomId, userId, placeId);
            case REMOVE_VOTE -> removeVote(roomId, userId, placeId);
            default -> throw new IllegalArgumentException("Unknown actionType: " + dto.getActionType());
        }

        List<CandidateMessageResponseDto> response = getCandidatesSortedByVotes(roomId, roomCode);
        messagingTemplate.convertAndSend("/topic/candidate." + roomCode, response);
    }

    public List<CandidateMessageResponseDto> getCandidatesSortedByVotes(Long roomId, String roomCode) {
        List<Long> sortedPlaceIds = candidateRepository.findCandidatePlaceIdsOrderByVoteCount(roomId);

        List<CandidateMessageResponseDto> result = new ArrayList<>();
        for (Long placeId : sortedPlaceIds) {
            Place place = placeRepository.findById(placeId)
                    .orElseThrow(() -> new IllegalArgumentException("place not found: " + placeId));

            List<Long> votedUserIds = voteRepository.findVotedUserIds(roomId, placeId);
            int voteCount = votedUserIds.size();

            result.add(CandidateMessageResponseDto.builder()
                    .roomCode(roomCode)
                    .place(PlaceResponse.from(place))
                    .votedUserIds(votedUserIds)
                    .voteCount(voteCount)
                    .build());
        }

        return result;
    }

    private void validate(CandidateMessageRequestDto dto, String roomCode, Long roomId) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new IllegalArgumentException("roomCode is required");
        }
        if (roomId == null) {
            throw new IllegalArgumentException("Invalid roomCode: cannot decode to roomId");
        }
        if (dto.getPlaceId() == null) {
            throw new IllegalArgumentException("placeId is required");
        }
        if (dto.getActionType() == null) {
            throw new IllegalArgumentException("actionType is required");
        }
        if (dto.getActionType() == CandidateActionType.ADD_VOTE ||
                dto.getActionType() == CandidateActionType.REMOVE_VOTE) {
            if (dto.getUserId() == null) {
                throw new IllegalArgumentException("userId is required for vote actions");
            }
        }
    }

    private void addPlace(Long roomId, Long placeId) {
        if (!candidateRepository.existsByRoomIdAndPlaceId(roomId, placeId)) {
            candidateRepository.save(CandidateMessage.builder()
                    .roomId(roomId)
                    .placeId(placeId)
                    .build());
        }
    }

    private void removePlace(Long roomId, Long placeId) {
        candidateRepository.deleteByRoomIdAndPlaceId(roomId, placeId);
        voteRepository.deleteByRoomIdAndPlaceId(roomId, placeId);
    }

    private void addVote(Long roomId, Long userId, Long placeId) {
        if (!voteRepository.existsByRoomIdAndUserIdAndPlaceId(roomId, userId, placeId)) {
            voteRepository.save(Vote.builder()
                    .roomId(roomId)
                    .userId(userId)
                    .placeId(placeId)
                    .build());
        }
    }

    private void removeVote(Long roomId, Long userId, Long placeId) {
        voteRepository.deleteByRoomIdAndUserIdAndPlaceId(roomId, userId, placeId);
    }
}
