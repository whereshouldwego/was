package com.example.whereshouldwego.dto.response;

import com.example.whereshouldwego.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class UserDto {

    private Long id;
    private String username;
    private String role;
    private String name;
    private String email;
    private String image;


    public static UserDto fromEntity(String username, String role, String name, String email, String image) {
        return UserDto.builder()
                .username(username)
                .role(role)
                .name(name)
                .email(email)
                .image(image)
                .build();
    }

    public static UserDto toEntity(User user) {
        return UserDto.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .name(user.getName())
                .email(user.getEmail())
                .image(user.getImage())
                .build();
    }

}
