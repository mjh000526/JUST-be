package com.example.just.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TestFcmRequestDto {
    private String title;
    private String body;
    private String token;
}
