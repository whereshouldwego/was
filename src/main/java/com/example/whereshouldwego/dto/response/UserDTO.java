package com.example.whereshouldwego.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Long id;
    private String username;
    private String role;
    private String name;
    private String email;
    private String image;
}
