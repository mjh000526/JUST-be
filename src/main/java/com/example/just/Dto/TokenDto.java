package com.example.just.Dto;

import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto { //토큰을 저장하고 사용할 클래스

    private String access_token;
    private String refresh_token;
}
