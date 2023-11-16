package com.example.just.Dto;
import com.example.just.Dao.Post;
import com.example.just.Dao.Role;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.*;


//로그인 할 때 사용할 Dto
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id; //회원 아이디

    private Timestamp create_time;

    private String email;

    private String provider;

    private Role role;
    private String provider_id;

    private int blamed_count;
    //신고한 횟수

    private int blame_count;

    @Builder.Default
    private List<Post> liked_posts = new ArrayList<>();

    @Builder.Default
    private List<Post> posts = new ArrayList<>();
}
