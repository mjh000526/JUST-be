package com.example.just.Dto;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ResponseMyCommentDto {
    private Long comment_id;
    private String comment_content;
    private ResponseGetPostDto post;
    private Date time;

    public ResponseMyCommentDto(Comment comment){
        comment_id = comment.getComment_id();
        comment_content = comment.getComment_content();
        post = new ResponseGetPostDto(comment.getPost());
        time = comment.getComment_create_time();
    }
}
