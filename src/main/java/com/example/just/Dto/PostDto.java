package com.example.just.Dto;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class PostDto {
    private Long post_id;

    private String post_content;    //글 내용

    private String post_tag;    //글 태그

    private Long post_like; //공감 회수

    private Long post_picture;

    @JsonIgnore
    private Timestamp post_create_time;  //글 생성 시간

    private boolean secret; //글 공개 여부


    private String emoticon;     //글 이모티콘


    private String post_category; //글 카테고리

    @JsonIgnore
    private List<Member> likedMembers = new ArrayList<>();

    @JsonIgnore // Swagger에 postDto만 넘기려고
    private Member member;  //글을쓴 Member_id

    @JsonIgnore
    private List<Comment> comments;

    private int blamedCount;



    public boolean getSecret() {
        return this.secret;
    }
}
