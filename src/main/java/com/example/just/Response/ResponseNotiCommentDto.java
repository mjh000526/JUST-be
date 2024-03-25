package com.example.just.Response;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Post;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseNotiCommentDto {

    private Long comment_id;

    private String comment_content;

    private int comment_count;

    private Long like_count;

    private Date comment_create_time;


    public ResponseNotiCommentDto(Comment comment){
        comment_id = comment.getComment_id();
        comment_content = comment.getComment_content();
        comment_count = comment.getChildren().size();
        like_count = comment.getComment_like();
        comment_create_time = comment.getComment_create_time();
    }
}
