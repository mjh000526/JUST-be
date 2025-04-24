package com.example.just.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponsePostCommentDtoBefore { //api의 v1에서 사용되는 게시글 내용과 댓글을 담는 포맷
    private List<String> post_content;
    private List<ResponseCommentDtoBefore> comments;

    public ResponsePostCommentDtoBefore(List<String> content, List<ResponseCommentDtoBefore> list){
        post_content = content;
        comments = list;
    }
}