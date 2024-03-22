package com.example.just.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FcmMessageRequestDto {
    private String title;
    private String body;
    private String targetToken;
}
