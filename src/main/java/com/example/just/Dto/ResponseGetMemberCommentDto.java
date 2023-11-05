package com.example.just.Dto;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGetMemberCommentDto {
    private String comment_content;
    private Long post_id;
    private Date time;
}
