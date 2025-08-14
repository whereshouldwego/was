package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.domain.Vote;
import com.example.whereshouldwego.dto.request.CandidateRequest;
import com.example.whereshouldwego.dto.response.CandidateResponse;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.dto.response.PlaceResponse;
import com.example.whereshouldwego.repository.postgres.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.whereshouldwego.util.RoomCodeUtil.decode;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final SimpMessagingTemplate messagingTemplate;
    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;
    private final RoomParticipantRepository roomParticipantRepository;

    @Transactional
    public List<CandidateResponse> handleIncomingCandidate(CandidateRequest dto,
                                        CustomUserDetails userDetails,
                                        String roomCode
    ) {
        Long roomId = decode(roomCode);
        Long userId = userDetails.getId();

        if (!roomParticipantRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new AccessDeniedException("User is not a member of the room");
        }

        switch (dto.getActionType()) {
            case ADD_PLACE -> savePlaceIfNotExists(roomId, dto);
            case REMOVE_PLACE -> removePlace(roomId, dto);
            case ADD_VOTE -> saveVoteIfNotExists(roomId, userId, dto);
            case REMOVE_VOTE -> removeVote(roomId, userId, dto);
            default -> throw new IllegalArgumentException("Unknown actionType: " + dto.getActionType());
        }

        return getCandidatesSortedByVotes(roomId, roomCode);
    }

    public void broadcastCandidates(String roomCode, List<CandidateResponse> response) {
        messagingTemplate.convertAndSend("/topic/candidate." + roomCode, response);
    }

    public List<CandidateResponse> getCandidatesSortedByVotes(Long roomId, String roomCode) {
        List<Object[]> rows = candidateRepository.findPlacesWithVoteCountFetchJoin(roomId);
        return rows.stream()
                .map(r -> CandidateResponse.fromEntity(
                        roomCode,
                        PlaceResponse.fromEntity((Place) r[0]),
                        ((Number) r[1]).intValue()
                ))
                .toList();
    }

    private void savePlaceIfNotExists(Long roomId, CandidateRequest dto) {
        try {
            candidateRepository.save(dto.toEntity(roomId));
        } catch (DataIntegrityViolationException ignore) {
            // 이미 존재하는 경우 무시
        }
    }

    private void removePlace(Long roomId, CandidateRequest dto) {
        candidateRepository.deleteByRoomIdAndPlaceId(roomId, dto.getPlaceId());
        voteRepository.deleteByRoomIdAndPlaceId(roomId, dto.getPlaceId());
    }

    private void saveVoteIfNotExists(Long roomId, Long userId, CandidateRequest dto) {
        try {
            voteRepository.save(Vote.builder()
                    .roomId(roomId)
                    .userId(userId)
                    .placeId(dto.getPlaceId())
                    .build());
        } catch (DataIntegrityViolationException ignore) {
            // 이미 투표한 경우 무시
        }
    }

    private void removeVote(Long roomId, Long userId, CandidateRequest dto) {
        voteRepository.deleteByRoomIdAndUserIdAndPlaceId(roomId, userId, dto.getPlaceId());
    }

    public List<CandidateResponse> getCandidateHistory(String roomCode) {
        Long roomId = decode(roomCode);
        return getCandidatesSortedByVotes(roomId, roomCode);
    }
}
