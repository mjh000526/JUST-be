package com.example.just.Response;

import com.example.just.Dao.HashTagMap;
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
    private boolean mine;

    public ResponseGetMemberPostDto(Post post, Long member_id, Member member) {
        this.post_id = post.getPost_id();
        this.post_content = post.getContents();
        this.post_create_time = post.getPost_create_time();
        this.secret = post.getSecret();
        List<HashTagMap> hashTagMaps = post.getHashTagMaps();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < hashTagMaps.size(); i++) {
            names.add(hashTagMaps.get(i).getHashTag().getName());
        }
        this.hash_tag = names;
        this.comment_size = Long.valueOf(post.getComments().size());
        this.post_picture = post.getPost_picture();
        this.post_like_size = post.getPost_like();
        this.blamed_count = Math.toIntExact(post.getBlamedCount());
        this.like = false;
        if (member.getLikedPosts().stream()
                .anyMatch(post2 -> post2.getPost_id().equals(post.getPost_id()))) {
            this.like = true;
        } else {
            this.like = false;
        }
    }


    public ResponseGetMemberPostDto(List<Post> results, Long member_id, int i, List<HashTagMap> hashTagMaps,
                                    Member member) {
        this.post_id = results.get(i).getPost_id();
        this.post_content = results.get(i).getContents();
        this.post_picture = results.get(i).getPost_picture();
        List<String> names = new ArrayList<>();
        for (int j = 0; j < hashTagMaps.size(); j++) {
            names.add(hashTagMaps.get(j).getHashTag().getName());
        }
        this.hash_tag = names;
        this.post_create_time = results.get(i).getPost_create_time();
        this.blamed_count = Math.toIntExact(results.get(i).getBlamedCount());
        this.secret = results.get(i).getSecret();
        this.post_like_size = results.get(i).getPost_like();
        this.comment_size = ((long) results.get(i).getComments().size());
        if (member_id != -1) {
            if (results.get(i).getMember().getId() == member_id) {
                this.mine = true;
            } else {
                this.mine = false;
            }
        }
        this.like = false;
        if (member != null) {
            if (member.getLikedPosts().stream()
                    .anyMatch(post -> post.getPost_id().equals(results.get(i).getPost_id()))) {
                this.like = true;
            } else {
                this.like = false;
            }
        }
    }
}