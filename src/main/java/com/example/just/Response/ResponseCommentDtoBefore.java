package com.example.just.Response;

import com.example.just.Dao.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ResponseCommentDtoBefore {//댓글 api의 v1 댓글 포맷
    private Long comment_id;
    private String comment_content;
    private Date comment_create_time;
    private Long comment_like;
    private Long comment_dislike;
    private ResponseCommentDtoBefore parent;
    private Integer blamed_count;
    private Boolean isMine;

    public ResponseCommentDtoBefore(Comment comment,Long member_id){
        comment_id = comment.getComment_id();
        comment_content = comment.getComment_content();
        comment_create_time = comment.getComment_create_time();
        comment_like = comment.getComment_like();
        comment_dislike = comment.getComment_dislike();
        blamed_count = comment.getBlamedCount();
        isMine = comment.getMember().getId() == member_id ? true : false;
        if(comment.getParent() != null){
            parent = new ResponseCommentDtoBefore(comment.getParent(), member_id);
        }
    }
}