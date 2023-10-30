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



@Getter
@Setter
public class CommentDto {

    private String comment_content; //댓글 내용

    private Long parentCommentId;
    public CommentDto() {
    }

    public CommentDto(Comment comment) {
        this.comment_content = comment_content;
        this.parentCommentId = comment.getParent() == null ? null : comment.getParent().getComment_id();
    }
}
