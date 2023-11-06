package com.example.just.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponsePostCommentDto {
    private List<String> post_content;
    private List<ResponseCommentDto> comments;

    public ResponsePostCommentDto(List<String> content, List<ResponseCommentDto> list){
        post_content = content;
        comments = list;
    }
}
