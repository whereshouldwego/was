package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Candidate;
import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.domain.Vote;
import com.example.whereshouldwego.dto.request.CandidateRequest;
import com.example.whereshouldwego.dto.response.CandidateResponse;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.dto.response.PlaceResponse;
import com.example.whereshouldwego.repository.postgres.CandidateRepository;
import com.example.whereshouldwego.repository.postgres.RoomParticipantRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import com.example.whereshouldwego.repository.postgres.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.whereshouldwego.util.RoomCodeUtil.decode;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final VoteRepository voteRepository;
    private final RoomParticipantRepository roomParticipantRepository;

    @Transactional(readOnly = true)
    public List<CandidateResponse> handleAndBroadcast(
            CandidateRequest request,
            CustomUserDetails user,
            String roomCode
    ) {
        Long roomId = decode(roomCode);
        Long userId = userRepository.findIdByUsername(user.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // 권한 확인
        if (!roomParticipantRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new AccessDeniedException("User is not a member of the room");
        }

        // 행위 처리
        switch (request.getActionType()) {
            case ADD_PLACE -> addPlaceIfAbsent(roomId, request);
            case REMOVE_PLACE -> removePlaceAndVotes(roomId, request);
            case ADD_VOTE -> addVoteIfAbsent(roomId, userId, request);
            case REMOVE_VOTE -> removeVote(roomId, userId, request);
        }

        return getCandidatesSortedByVotes(roomId, roomCode);
    }

    public void broadcastCandidates(String roomCode, List<CandidateResponse> response) {
        messagingTemplate.convertAndSend("/topic/candidate." + roomCode, response);
    }

    public List<CandidateResponse> getCandidatesSortedByVotes(Long roomId, String roomCode) {
        List<Object[]> rows = candidateRepository.findPlacesWithVoteCountFetchJoin(roomId);
        return rows.stream()
                .map(r -> CandidateResponse.of(
                        roomCode,
                        PlaceResponse.fromEntity((Place) r[0]),
                        ((Number) r[1]).intValue()
                ))
                .toList();
    }

    private void addPlaceIfAbsent(Long roomId, CandidateRequest request) {
        try {
            Candidate entity = Candidate.of(roomId, request.getPlaceId());
            candidateRepository.save(entity);
        } catch (DataIntegrityViolationException ignore) {
        }
    }

    private void removePlaceAndVotes(Long roomId, CandidateRequest request) {
        candidateRepository.deleteByRoomIdAndPlaceId(roomId, request.getPlaceId());
        voteRepository.deleteByRoomIdAndPlaceId(roomId, request.getPlaceId());
    }

    private void addVoteIfAbsent(Long roomId, Long userId, CandidateRequest request) {
        try {
            voteRepository.save(Vote.builder()
                    .roomId(roomId)
                    .userId(userId)
                    .placeId(request.getPlaceId())
                    .build());
        } catch (DataIntegrityViolationException ignore) {
        }
    }

    private void removeVote(Long roomId, Long userId, CandidateRequest request) {
        voteRepository.deleteByRoomIdAndUserIdAndPlaceId(roomId, userId, request.getPlaceId());
    }

    public List<CandidateResponse> getCandidateHistory(String roomCode) {
        Long roomId = decode(roomCode);
        return getCandidatesSortedByVotes(roomId, roomCode);
    }
}
