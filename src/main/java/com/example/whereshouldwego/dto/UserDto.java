package com.example.whereshouldwego.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class UserDto {

    private String name;
    private String username;
    private String role;

    public static UserDto fromEntity(String name, String username, String role) {
        return UserDto.builder()
                .name(name)
                .username(username)
                .role(role)
                .build();
    }
}
