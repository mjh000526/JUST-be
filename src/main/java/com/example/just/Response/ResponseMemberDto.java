package com.example.just.Response;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMemberDto {//로그인기능 요청시 응답할 회원 정보 포맷
    String email;
    String nickname;
}
