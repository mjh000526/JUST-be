package com.example.just.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TestFcmRequestDto {//fcm요청을 위한 테스트용포맷
    private String title;
    private String body;
    private String token;
}
