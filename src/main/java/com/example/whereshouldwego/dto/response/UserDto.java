package com.example.whereshouldwego.dto.response;

import com.example.whereshouldwego.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {

    private String username;
    private String role;

    public static UserDto from(User user) {
        return UserDto.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public static UserDto of(String username, String role) {
        return UserDto.builder()
                .username(username)
                .role(role)
                .build();
    }
}
