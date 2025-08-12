package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.CustomOAuth2User;
import com.example.whereshouldwego.dto.UserDto;
import com.example.whereshouldwego.dto.response.KakaoResponse;
import com.example.whereshouldwego.dto.response.OAuth2Response;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("kakao")) {

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듦
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        Optional<User> existUser = userRepository.findByUsername(username);

        if (existUser.isEmpty()) {

            User user = User.builder()
                    .username(username)
                    .role("ROLE_USER")
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .image(oAuth2Response.getImage())
                    .build();
            userRepository.save(user);

            return new CustomOAuth2User(UserDto.fromEntity(username, oAuth2Response.getName(), "ROLE_USER"));
        }
        else {

            User user = existUser.get();

            User updatedUser = user.toBuilder()
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .image(oAuth2Response.getImage())
                    .build();
            userRepository.save(updatedUser);

            return new CustomOAuth2User(UserDto.fromEntity(updatedUser.getUsername(), oAuth2Response.getName(), updatedUser.getRole()));
        }
    }
}
