package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Candidate;
import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.domain.Vote;
import com.example.whereshouldwego.dto.request.CandidateRequest;
import com.example.whereshouldwego.dto.response.CandidateItemResponse;
import com.example.whereshouldwego.dto.response.CandidateResponse;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.dto.response.PlaceResponse;
import com.example.whereshouldwego.repository.postgres.CandidateRepository;
import com.example.whereshouldwego.repository.postgres.RoomParticipantRepository;
import com.example.whereshouldwego.repository.postgres.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
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

    @Transactional
    public CandidateResponse handleCandidates(
            CandidateRequest request,
            Long userId,
            String roomCode
    ) {
        Long roomId = decode(roomCode);

        // 행위 처리
        switch (request.getActionType()) {
            case ADD_PLACE -> addPlaceIfAbsent(roomId, request.getPlaceId());
            case REMOVE_PLACE -> removePlaceAndVotes(roomId, request.getPlaceId());
            case ADD_VOTE -> addVoteIfAbsent(roomId, userId, request.getPlaceId());
            case REMOVE_VOTE -> removeVote(roomId, userId, request.getPlaceId());
        }

        return getCandidatesSortedByVotes(roomId, roomCode);
    }

    public void broadcastCandidates(String roomCode, CandidateResponse response) {
        messagingTemplate.convertAndSend("/topic/candidate." + roomCode, response);
    }

    public CandidateResponse getCandidatesSortedByVotes(Long roomId, String roomCode) {
        List<Object[]> rows = candidateRepository.findPlacesWithVoteCountFetchJoin(roomId);
        List<CandidateItemResponse> items = rows.stream()
                .map(r -> CandidateItemResponse.of(
                        PlaceResponse.fromEntity((Place) r[0]),
                        ((Number) r[1]).intValue()
                ))
                .toList();
        return CandidateResponse.of(roomCode, items);
    }

    private void addPlaceIfAbsent(Long roomId, Long placeId) {
        try {
            candidateRepository.save(Candidate.of(roomId, placeId));
        } catch (DataIntegrityViolationException ignore) {
        }
    }

    private void removePlaceAndVotes(Long roomId, Long placeId) {
        voteRepository.deleteByRoomIdAndPlaceId(roomId, placeId);
        candidateRepository.deleteByRoomIdAndPlaceId(roomId, placeId);
    }

    private void addVoteIfAbsent(Long roomId, Long userId, Long placeId) {
        if (!candidateRepository.existsByRoomIdAndPlaceId(roomId, placeId)) {
            return;
        }
        try {
            voteRepository.save(Vote.of(roomId, userId, placeId));
        } catch (DataIntegrityViolationException ignore) {
        }
    }

    private void removeVote(Long roomId, Long userId, Long placeId) {
        voteRepository.deleteByRoomIdAndUserIdAndPlaceId(roomId, userId, placeId);
    }

    public CandidateResponse getCandidateHistory(String roomCode) {
        Long roomId = decode(roomCode);
        return getCandidatesSortedByVotes(roomId, roomCode);
    }
}
