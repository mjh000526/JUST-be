package com.example.just.Dto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import lombok.*;
 
//로그인 할 때 사용할 Dto
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private String email;
    private String provider;

}
//어디쓰는지 모르겠어서 일단 주석해놨음
/*
@Getter
@Setter
public class MemberDto {
    private Long member_id; //회원 아이디

    private String member_email;    //회원 메일

    private Date member_register;   //회원 등록일

    private String password; //비밀번호

    private String provider; //인증

    private String role; //권한

    private String username; //이름
}*/
