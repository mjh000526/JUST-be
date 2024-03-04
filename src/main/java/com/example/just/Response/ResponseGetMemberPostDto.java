package com.example.just.Response;

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
    private boolean mine;

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

    public ResponseGetMemberPostDto(List<Post> results, Long member_id, int i) {

        this.post_id = results.get(i).getPost_id();
        this.post_content = results.get(i).getPostContent();
        this.post_picture = results.get(i).getPost_picture();
        List<String> names = new ArrayList<>();
        List<HashTag> hashTags = results.get(i).getHash_tag();
        for (int j = 0; j < hashTags.size(); j++) {
            names.add(hashTags.get(j).getName());
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

    }
}