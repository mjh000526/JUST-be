package com.example.just.Response;

import com.example.just.Document.PostDocument;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseSearchDto {

    private Long post_id;
    private List<String> post_content;
    private List<String> hash_tag;
    private Long post_picture;
    private Date post_create_time;
    private Boolean secret;
    private Long comment_size;
    private Long post_like_size;
    private Long blamed_count;
    private Boolean like;
    private Boolean mine;

    public ResponseSearchDto(){

    }

    public ResponseSearchDto(PostDocument postDocument, Long member_id){
        post_id = postDocument.getId();
        post_content = postDocument.getPost_content();
        hash_tag = postDocument.getHashTag();
        post_picture = postDocument.getPost_picture();
        post_create_time = postDocument.getPost_create_time();
        secret = postDocument.getSecret();
        comment_size = postDocument.getComment_size();
        post_like_size = postDocument.getPost_like_size();
        blamed_count = postDocument.getBlamed_count();
        like = false; //윌이 수정하면 따라하기
        mine = postDocument.getMember_id() == member_id ? true : false;
    }
}
