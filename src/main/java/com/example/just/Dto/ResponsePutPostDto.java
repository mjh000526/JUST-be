package com.example.just.Dto;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponsePutPostDto {


    private Long post_id;

    private List<String> post_content;    //글 내용

    @Schema(name = "post_tag", example = "오늘, 바다, 내일")
    private String post_tag;    //글 태그

    private Long post_picture;

    private boolean secret; //글 공개 여부

    @Schema(name = "post_category", example = "직장인")
    private String post_category; //글 카테고리

    @JsonIgnore
    private List<Member> liked_members = new ArrayList<>();

    @JsonIgnore // Swagger에 postDto만 넘기려고
    private Member member;  //글을쓴 Member_id

    @JsonIgnore
    private List<Comment> comments;


    public boolean getSecret() {
        return this.secret;
    }

    public ResponsePutPostDto(String post_category, List<String> post_content, Long post_id, Long post_picture,
                              String post_tag, boolean secret) {
        this.post_category = post_category;
        this.post_content = post_content;
        this.post_id = post_id;
        this.post_picture = post_picture;
        this.post_tag = post_tag;
        this.secret = secret;
    }
}
