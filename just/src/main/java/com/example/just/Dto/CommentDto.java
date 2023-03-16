package com.example.just.Dto;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.just.Dao.QComment.comment;

@Getter
@Setter
public class CommentDto {
    private Long comment_id;    //댓글 아이디

    private String comment_content; //댓글 내용

    private LocalDateTime comment_create_time;   //댓글 작성 시간

    private Long comment_like;  //추천수

    private Long comment_dislike;   //비추천수

    private Long member_id;

    private Long parentCommentId;
    public CommentDto() {
    }

    public CommentDto(Comment comment) {
        this.comment_id = comment_id;
        this.comment_content = comment_content;
        this.comment_create_time = comment_create_time;
        this.comment_like = comment_like;
        this.comment_dislike = comment_dislike;
        this.member_id=member_id;
        this.parentCommentId = comment.getParent() == null ? null : comment.getParent().getComment_id();
    }
}
