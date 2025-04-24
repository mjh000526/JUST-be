package com.example.just.Response;

import com.example.just.Dao.Post;
import com.example.just.Dao.PostContent;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseNotiPostDto {//게시글에 대한 알림 응답에 대한 데이터 포맷
    private Long post_id;

    private List<PostContent> post_content;

    private Long post_picture;

    private int comment_count;

    private Long like_count;

    private Date post_create_time;

    private boolean secret;

    public ResponseNotiPostDto(Post post){
        post_id = post.getPost_id();
        post_content = post.getPostContent();
        post_picture = post.getPost_picture();
        comment_count = post.getComments().size();
        like_count = post.getPost_like();
        post_create_time = post.getPost_create_time();
        secret = post.getSecret();
    }
}
