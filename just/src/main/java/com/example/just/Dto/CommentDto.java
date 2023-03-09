package com.example.just.Dto;

import javax.persistence.Column;
import java.util.Date;

public class CommentDto {
    private Long comment_id;    //댓글 아이디

    private String comment_content; //댓글 내용

    private Date comment_create_time;   //댓글 작성 시간

    private String comment_reply;   //응답여부

    private Long comment_like;  //추천수

    private Long comment_dislike;   //비추천수

    private Long post_id; //댓글이 달린 게시물
}
