package com.example.just.Response;

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
