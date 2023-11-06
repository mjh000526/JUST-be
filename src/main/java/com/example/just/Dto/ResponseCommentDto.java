package com.example.just.Dto;

import com.example.just.Dao.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ResponseCommentDto {
    private Long comment_id;
    private String comment_content;
    private Date comment_create_time;
    private Long comment_like;
    private Long comment_dislike;
    private List<ResponseCommentDto> parent = null;
    private Integer blamed_count;
    private Boolean isMine;

    public ResponseCommentDto(Comment comment,Long member_id){
        comment_id = comment.getComment_id();
        comment_content = comment.getComment_content();
        comment_create_time = comment.getComment_create_time();
        comment_like = comment.getComment_like();
        comment_dislike = comment.getComment_dislike();
        blamed_count = comment.getBlamedCount();
        isMine = comment.getMember().getId() == member_id ? true : false;
        if(comment.getParent() != null){
            parent = comment.getChildren().stream()
                    .map(child_comment -> new ResponseCommentDto(child_comment, member_id))
                    .collect(Collectors.toList());
        }
    }
}
