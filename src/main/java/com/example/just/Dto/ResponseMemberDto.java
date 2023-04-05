package com.example.just.Dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMemberDto {
    String email;
    String nickname;
}
