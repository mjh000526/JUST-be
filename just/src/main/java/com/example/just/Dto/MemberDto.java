package com.example.just.Dto;
import com.example.just.Dao.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.*;
 
//로그인 할 때 사용할 Dto
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long member_id; //회원 아이디

    private String email;
    private String provider;

}
