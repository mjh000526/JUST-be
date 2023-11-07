package com.example.just.Dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.example.just.Dao.Post;
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

    private Long post_like_size;
    private int blamed_count;

    public ResponseGetPostDto(){}
    public ResponseGetPostDto(Post post){
        post_id = post.getPost_id();
        post_content = post.getPostContent().stream()
                .map(conent -> new String(conent))
                .collect(Collectors.toList());
        post_tag = post.getPost_tag();
        post_picture = post.getPost_picture();
        post_create_time = post.getPost_create_time();
        secret = post.getSecret();
        post_category = post.getPost_category();
        comment_size = (long) post.getComments().size();
        blamed_count = post.getBlamedCount();
    }
}
