package com.example.just.Dto;

import com.example.just.Dao.HashTag;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGetMemberPostDto {
    private Long post_id;

    private List<String> post_content;
    private List<String> hash_tag;    //글 태그

    private Long post_picture;

    private Date post_create_time;  //글 생성 시간

    private boolean secret; //글 공개 여부

    private String post_category; //글 카테고리

    private Long comment_size;

    private Long post_like_size;
    private int blamed_count;
    private boolean like;

    public ResponseGetMemberPostDto(Post post, Long member_id, Member member) {
        this.post_id = post.getPost_id();
        this.post_content = post.getPostContent();
        this.post_create_time = post.getPost_create_time();
        this.secret = post.getSecret();
        List<String> names = new ArrayList<>();
        List<HashTag> hashTags = post.getHash_tag();
        for (int j = 0; j < hashTags.size(); j++) {
            names.add(hashTags.get(j).getName());
        }
        this.hash_tag = names;
        this.comment_size = Long.valueOf(post.getComments().size());
        this.post_picture = post.getPost_picture();
        this.post_like_size = post.getPost_like();
        this.blamed_count = Math.toIntExact(post.getBlamedCount());
        this.like = false;
        for (int i = 0; i < post.getLikedMembers().size(); i++) {
            System.out.println(post.getLikedMembers().get(i).getId());
            if (post.getLikedMembers().get(i).getId() == member_id) {

                this.like = true;
                break;
            }
        }
    }

    public ResponseGetMemberPostDto() {

    }
}