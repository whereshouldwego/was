package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.CustomUserDetails;
import com.example.whereshouldwego.dto.UserDTO;
import com.example.whereshouldwego.dto.response.KakaoResponse;
import com.example.whereshouldwego.dto.response.OAuth2Response;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

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

            User user = new User();
            user.setUsername(username);
            user.setName(oAuth2Response.getName());
            user.setEmail(oAuth2Response.getEmail());
            user.setImage(oAuth2Response.getImage());
            user.setRole("ROLE_USER");

            userRepository.save(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole("ROLE_USER");

            return new CustomUserDetails(userDTO);
        }
        else {

            User user = existUser.get();

            user.setName(oAuth2Response.getName());
            user.setEmail(oAuth2Response.getEmail());
            user.setImage(oAuth2Response.getImage());

            userRepository.save(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(user.getUsername());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(user.getRole());

            return new CustomUserDetails(userDTO);
        }
    }
}
