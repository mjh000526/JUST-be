package com.example.just.Response;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ResponseMyCommentDto {//클라이언트에게 응답할 때 클라이언트가 작성한 댓글인지 여부를 포함하는 데이터 포맷
    private Long comment_id;
    private String comment_content;
    private ResponseGetMemberPostDto post;
    private Date time;

    private boolean isMine;

    public ResponseMyCommentDto(Comment comment, Long member_id, Member member, boolean isMine){
        comment_id = comment.getComment_id();
        comment_content = comment.getComment_content();
        post = new ResponseGetMemberPostDto(comment.getPost(), member_id, member);
        time = comment.getComment_create_time();
        this.isMine = isMine;
    }
}