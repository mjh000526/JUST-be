package com.example.just.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutCommentDto {
    private String comment_content; //댓글 내용

    public PutCommentDto(){}

    public PutCommentDto(String content){
        comment_content = content;
    }
}
