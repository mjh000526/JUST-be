package com.example.just.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponsePostCommentDtoBefore {
    private List<String> post_content;
    private List<ResponseCommentDtoBefore> comments;

    public ResponsePostCommentDtoBefore(List<String> content, List<ResponseCommentDtoBefore> list){
        post_content = content;
        comments = list;
    }
}