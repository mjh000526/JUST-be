package com.example.just.Dto;
import com.example.just.Dao.Post;
import com.example.just.Dao.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

//로그인 할 때 사용할 Dto
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id; //회원 아이디

    private Timestamp createTime;

    private String email;

    private String provider;

    private Role role;
    private String provider_id;

    private int blamedCount;
    //신고한 횟수

    private int blameCount;

    private List<Post> likedPosts = new ArrayList<>();

    private List<Post> posts = new ArrayList<>();
}
