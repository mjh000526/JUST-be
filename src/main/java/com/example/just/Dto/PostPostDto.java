package com.example.just.Dto;

import com.example.just.Dao.Comment;
import com.example.just.Dao.HashTag;
import com.example.just.Dao.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

//
public class PostPostDto {

    private List<String> post_content;

    private List<String> hash_tage;    //글 태그

    private Long post_picture;

    @JsonIgnore
    private Timestamp post_create_time;  //글 생성 시간

    private boolean secret; //글 공개 여부

    @JsonIgnore
    private List<Member> liked_members = new ArrayList<>();

    @JsonIgnore // Swagger에 postDto만 넘기려고
    private Member member;  //글을쓴 Member_id

    @JsonIgnore
    private List<Comment> comments;


    public boolean getSecret() {
        return this.secret;
    }
}
