package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.domain.Vote;
import com.example.whereshouldwego.domain.type.CandidateActionType;
import com.example.whereshouldwego.dto.request.CandidateMessageRequestDto;
import com.example.whereshouldwego.dto.response.CandidateMessageResponseDto;
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

        switch (dto.getActionType()) {
            case ADD_PLACE -> addPlace(roomId, dto);
            case REMOVE_PLACE -> removePlace(roomId, dto);
            case ADD_VOTE -> addVote(roomId, dto);
            case REMOVE_VOTE -> removeVote(roomId, dto);
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
            result.add(CandidateMessageResponseDto.fromEntity(roomCode, place, votedUserIds));
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

    private void addPlace(Long roomId, CandidateMessageRequestDto dto) {
        if (!candidateRepository.existsByRoomIdAndPlaceId(roomId, dto.getPlaceId())) {
            candidateRepository.save(dto.toEntity(roomId));
        }
    }

    private void removePlace(Long roomId, CandidateMessageRequestDto dto) {
        candidateRepository.deleteByRoomIdAndPlaceId(roomId, dto.getPlaceId());
        voteRepository.deleteByRoomIdAndPlaceId(roomId, dto.getPlaceId());
    }

    private void addVote(Long roomId, CandidateMessageRequestDto dto) {
        if (!voteRepository.existsByRoomIdAndUserIdAndPlaceId(roomId, dto.getUserId(), dto.getPlaceId())) {
            voteRepository.save(Vote.builder()
                    .roomId(roomId)
                    .userId(dto.getUserId())
                    .placeId(dto.getPlaceId())
                    .build());
        }
    }

    private void removeVote(Long roomId, CandidateMessageRequestDto dto) {
        voteRepository.deleteByRoomIdAndUserIdAndPlaceId(roomId, dto.getUserId(), dto.getPlaceId());
    }

    public List<CandidateMessageResponseDto> getCandidateHistory(String roomCode) {
        Long roomId = decode(roomCode);
        return getCandidatesSortedByVotes(roomId, roomCode);
    }
}
