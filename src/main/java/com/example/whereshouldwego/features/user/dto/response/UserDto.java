package com.example.whereshouldwego.features.user.dto.response;

import com.example.whereshouldwego.features.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {

    private Long userId;
    private String role;

    public static UserDto from(User user) {
        return UserDto.builder()
                .userId(user.getId())
                .role(user.getRole())
                .build();
    }

    public static UserDto of(Long userId, String role) {
        return UserDto.builder()
                .userId(userId)
                .role(role)
                .build();
    }
}
