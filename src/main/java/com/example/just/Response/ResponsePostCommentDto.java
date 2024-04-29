package com.example.just.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponsePostCommentDto { //게시글 내용과 댓글들을 한번에 담기 위한 클래스
    private List<String> post_content;
    private List<ResponseCommentDto> comments;

    public ResponsePostCommentDto(List<String> content, List<ResponseCommentDto> list){
        post_content = content;
        comments = list;
    }
}
