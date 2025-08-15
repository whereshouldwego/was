package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.dto.response.KakaoResponse;
import com.example.whereshouldwego.dto.response.OAuth2Response;
import com.example.whereshouldwego.dto.response.UserDto;
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

        // 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듦
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        Optional<User> existData = userRepository.findByUsername(username);

        User user;

        if (existData.isEmpty()) {

            user = User.builder()
                    .username(username)
                    .role("ROLE_MEMBER")
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .image(oAuth2Response.getImage())
                    .build();

            userRepository.save(user);

            return new CustomUserDetails(UserDto.toEntity(username, "ROLE_MEMBER"));
        } else {

            user = existData.get();

            user.updateToSocialUser(
                    user.getUsername(),
                    user.getRole(),
                    oAuth2Response.getName(),
                    oAuth2Response.getEmail(),
                    oAuth2Response.getImage()
            );

            userRepository.save(user);

            return new CustomUserDetails(UserDto.toEntity(user.getUsername(), user.getRole()));
        }
    }
}
