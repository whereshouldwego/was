package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.response.*;
import com.example.whereshouldwego.jwt.JWTUtil;
import com.example.whereshouldwego.repository.postgres.RoomParticipantRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final JWTUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("kakao")) {

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {

            return null;
        }

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듦 (소셜 로그인 username)
        String socialUsername = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        Optional<User> existSocialUser = userRepository.findByUsername(socialUsername);

        User currentUser; // 최종적으로 인증될 사용자 객체

        // 소셜 로그인 username으로 기존 회원이 존재하는지 확인
        if (existSocialUser.isEmpty()) {

            // 소셜 로그인으로 처음 가입하는 경우

            // HTTP 요청 쿠키에서 기존 비회원 JWT 토큰을 찾아 비회원 유저를 조회
            Optional<User> guestUserFromCookie = findGuestUserFromCookie();

            if (guestUserFromCookie.isPresent()) {

                // 비회원 -> 회원으로 전환
                User oldGuestUser = guestUserFromCookie.get(); // 기존 비회원 유저 엔티티

                // RoomParticipant 정보 이전
                roomParticipantRepository.findAllByUser(oldGuestUser).forEach(rp -> {
                    rp.updateUser(oldGuestUser);
                    roomParticipantRepository.save(rp);
                });

                // 기존 비회원 유저의 정보를 소셜 로그인 정보로 업데이트 (ROLE_GUEST -> ROLE_USER)
                currentUser = oldGuestUser.toBuilder()
                        .username(socialUsername)
                        .role("ROLE_USER")
                        .name(oAuth2Response.getName())
                        .email(oAuth2Response.getEmail())
                        .image(oAuth2Response.getImage())
                        .build();

                // 업데이트된 비회원 유저를 DB에 저장 (기존 엔티티 업데이트)
                currentUser = userRepository.save(oldGuestUser);

            } else {

                // 쿠키에 유효한 비회원 토큰이 없어서 새로운 회원으로 가입하는 경우
                currentUser = User.builder()
                        .username(socialUsername)
                        .role("ROLE_USER")
                        .name(oAuth2Response.getName())
                        .email(oAuth2Response.getEmail())
                        .image(oAuth2Response.getImage())
                        .build();

                userRepository.save(currentUser);
            }
        } else {

            // 이미 가입한 소셜 회원이 다시 로그인하는 경우
            currentUser = existSocialUser.get();

            // 기존 회원 정보 업데이트 (이름, 이메일, 이미지 등 변경될 수 있는 정보)
            currentUser = currentUser.toBuilder()
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .image(oAuth2Response.getImage())
                    .build();

            userRepository.save(currentUser); // 변경된 엔티티 저장 (merge)
        }

        // CustomUserDetails에 담을 UserDTO 생성
        UserDTO userDTO = new UserDTO();
        userDTO.setId(currentUser.getId());
        userDTO.setUsername(currentUser.getUsername());
        userDTO.setRole(currentUser.getRole());
        userDTO.setName(currentUser.getName());
        userDTO.setEmail(currentUser.getEmail());
        userDTO.setImage(currentUser.getImage());

        return new CustomUserDetails(userDTO);
    }

    /**
     * HTTP 요청 쿠키에서 "refresh" 토큰을 찾아 비회원 사용자 정보를 반환합니다.
     * @return 유효한 비회원 사용자가 존재하면 Optional.of(User), 그렇지 않으면 Optional.empty()
     */
    private Optional<User> findGuestUserFromCookie() {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return Optional.empty(); // 요청 컨텍스트가 없는 경우
        }
        HttpServletRequest request = attributes.getRequest();
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    String refresh = cookie.getValue();
                    try {
                        // 토큰이 만료되었거나, 카테고리가 "refresh"가 아니면 유효하지 않다고 판단
                        if (jwtUtil.isExpired(refresh) || !jwtUtil.getCategory(refresh).equals("refresh")) {
                            return Optional.empty();
                        }
                        String username = jwtUtil.getUsername(refresh);
                        // username이 "guest "로 시작하는 비회원 형식인지 확인
                        if (username != null && username.startsWith("guest ")) {
                            return userRepository.findByUsername(username); // 비회원 사용자를 찾아 반환
                        }
                    } catch (ExpiredJwtException e) {
                        // 만료된 토큰인 경우
                        return Optional.empty();
                    } catch (Exception e) {
                        // 그 외 JWT 파싱 또는 검증 중 발생할 수 있는 다른 예외 처리
                        return Optional.empty();
                    }
                }
            }
        }

        return Optional.empty(); // 유효한 비회원 토큰을 찾지 못한 경우
    }
}
