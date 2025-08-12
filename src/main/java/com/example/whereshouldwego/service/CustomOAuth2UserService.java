package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.response.*;
import com.example.whereshouldwego.jwt.JWTUtil;
import com.example.whereshouldwego.repository.postgres.RoomParticipantRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        Optional<User> existData = userRepository.findByUsername(username);

        User user;

        if (existData.isEmpty()) {

            user = User.builder()
                    .username(username)
                    .role("ROLE_USER")
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .image(oAuth2Response.getImage())
                    .build();

            userRepository.save(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setRole("ROLE_USER");
            userDTO.setName(oAuth2User.getName());

            return new CustomUserDetails(userDTO);
        } else {

            user = existData.get();

            user.updateToSocialUser(
                    username,
                    "ROLE_USER",
                    oAuth2Response.getName(),
                    oAuth2Response.getEmail(),
                    oAuth2Response.getImage()
            );

            userRepository.save(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(user.getUsername());
            userDTO.setRole(user.getRole());
            userDTO.setName(oAuth2User.getName());

            return new CustomUserDetails(userDTO);
        }
    }
}