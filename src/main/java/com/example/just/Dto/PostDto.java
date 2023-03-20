package com.example.just.Dto;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Notification;
import com.example.just.Dao.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class PostDto {
    private Long post_id;

    private String post_content;    //글 내용

    private String post_tag;    //글 태그

    private Long post_like; //공감 회수

    private LocalDateTime post_create_time;  //글 생성 시간

    private boolean secret; //글 공개 여부


    private String emoticon;     //글 이모티콘


    private Long post_category; //글 카테고리

    private List<Post> likedPosts = new ArrayList<>();

    @JsonIgnore // Swagger에 postDto만 넘기려고
    private Member member;  //글을쓴 Member_id

    private List<Comment> comments;

    public PostDto(String content, String tag, int i, LocalDateTime now, boolean b, Object o, Object o1) {
    }


    public boolean isSecret() {
        return secret;
    }
}
