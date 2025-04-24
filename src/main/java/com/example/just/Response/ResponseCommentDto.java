package com.example.just.Response;

import com.example.just.Dao.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ResponseCommentDto {//클라이언트에 응답할 댓글 포맷
    private Long comment_id;
    private String comment_content;
    private Date comment_create_time;
    private Long comment_like;
    private Long comment_dislike;
    private Integer blamed_count;
    private Boolean isMine;
    private Boolean isPoster;
    private List<ResponseCommentDto> child;
    private String message;

    public ResponseCommentDto(Comment comment,Long member_id,String message){
        comment_id = comment.getComment_id();
        comment_content = comment.getComment_content();
        comment_create_time = comment.getComment_create_time();
        comment_like = comment.getComment_like();
        comment_dislike = comment.getComment_dislike();
        blamed_count = comment.getBlamedCount();
        isMine = comment.getMember().getId() == member_id ? true : false;
        isPoster = comment.getPost().getMember().getId() == member_id ? true : false;
        child = convertChildComments(comment.getChildren(), member_id);
        this.message = message;
    }
    public ResponseCommentDto(String message){
        comment_id = 0L;
        comment_content = null;
        comment_create_time = null;
        comment_like = null;
        comment_dislike = null;
        blamed_count = null;
        isMine = true;
        isPoster = true;
        child = null;
        this.message = message;
    }

    private List<ResponseCommentDto> convertChildComments(List<Comment> childComments, Long member_id) {
        if (childComments == null) {
            return new ArrayList<>();
        }

        return childComments.stream()
                .map(childComment -> new ResponseCommentDto(childComment, member_id,""))
                .collect(Collectors.toList());
    }
}

