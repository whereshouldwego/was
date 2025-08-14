package com.example.whereshouldwego.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {

    private String username;
    private String role;


    public static UserDto fromEntity(String username, String role) {
        return UserDto.builder()
                .username(username)
                .role(role)
                .build();
    }

    public static UserDto toEntity(String username, String role) {
        return UserDto.builder()
                .username(username)
                .role(role)
                .build();
    }

}
