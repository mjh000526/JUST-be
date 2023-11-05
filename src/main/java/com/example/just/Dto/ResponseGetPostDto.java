package com.example.just.Dto;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGetPostDto {
    private Long post_id;

    private List<String> post_content;
    private String post_tag;    //글 태그

    private Long post_picture;

    private Date post_create_time;  //글 생성 시간

    private boolean secret; //글 공개 여부

    private String post_category; //글 카테고리

    private Long comment_size;

    private Long post_like;
    private int blamed_count;
}
