package com.example.just.Dto;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ResponseMyCommentDto {
    private Long comment_id;
    private String comment_content;
    private ResponseGetMemberPostDto post;
    private Date time;

    public ResponseMyCommentDto(Comment comment, Long member_id, Member member){
        comment_id = comment.getComment_id();
        comment_content = comment.getComment_content();
        post = new ResponseGetMemberPostDto(comment.getPost(), member_id, member);
        time = comment.getComment_create_time();
    }
}