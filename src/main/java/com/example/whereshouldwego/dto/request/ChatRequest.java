package com.example.whereshouldwego.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequest {
    @NotBlank private String username;
    @NotBlank private String content;
}