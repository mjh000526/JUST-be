package com.example.just.Dao;

import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
@Data
@Setter
public class Post { //DB 게시글 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
    @JsonIgnore
    private List<PostContent> postContent;

    @Column(name = "post_picture")
    private Long post_picture;  //게시글 배경 사진 번호

    @CreationTimestamp
    @Column(name = "post_create_time")
    private Date post_create_time;  //글 생성 시간

    @Column(name = "post_like")
    private Long post_like; //좋아요 받은 갯수

    @Column(name = "secret")
    private boolean secret; //비밀글 여부

    @Column(name = "emoticon")
    private String emoticon;


    @ManyToMany()
    @JoinTable(
            name = "post_like",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @JsonIgnore
    @Builder.Default
    private List<Member> likedMembers = new ArrayList<>();  //좋아요를 누른 회원 리스트

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<HashTagMap> hashTagMaps = new ArrayList<>();//태그 N:M테이블

    @ManyToOne()
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;  //글을쓴 Member_id

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); //게시글에 작성된 댓글 리스트

    @Column(name = "blamed_count")
    private Long blamedCount;   //신고받은 횟수


    @PrePersist
    public void prePersist() {
        this.post_like = this.post_like == null ? 0L : this.post_like;
        this.emoticon = this.emoticon == null ? "0" : this.emoticon;
    }

    public void writePost(PostPostDto postDto, List<PostContent> postContent, Member member) { // 글 쓰기 생성자
        this.postContent = postContent;
        this.post_picture = postDto.getPost_picture();
        this.secret = postDto.getSecret();
        this.emoticon = "";
        this.post_like = 0L;
        this.member = member;
        this.blamedCount = 0L;
    }

    public void updatePost(String post_tag, Long post_like, Date post_create_time,
                           boolean secret, String emoticon, String post_category, Member member) {
        this.post_like = post_like;
        this.post_create_time = post_create_time;
        this.secret = secret;
        this.emoticon = emoticon;
        this.member = member;
    }

    public void addLike(Member member) {
        if (!likedMembers.contains(member)) {
            member.getLikedPosts().add(this);//좋아한 글 List에 해당 글의 객체 추가
            post_like++;
        }
    }

    public List<String> getContents(){
        return this.postContent.stream().map(n -> n.getContent()).collect(Collectors.toList());
    }

    public void removeLike(Member member) {
        if (likedMembers.contains(member)) {
            member.getLikedPosts().remove(this);
            this.likedMembers.remove(member);
            post_like--;
        }
    }


    public void addBlamed() {
        blamedCount++;
    }


    public boolean getSecret() {
        return this.secret;
    }

    public void changePost(PutPostDto postDto, Member member, Post post, List<PostContent> postContent) {
        this.post_id = post.getPost_id();
        this.member = member;
        this.setPost_create_time(new Date(System.currentTimeMillis()));
        this.setPost_like(post.getPost_like());
        this.post_picture = postDto.getPost_picture();
        this.secret = postDto.getSecret();
        this.postContent = postContent;
        this.hashTagMaps = new ArrayList<>();
        for (int i = 0; i < postDto.getHash_tag().size(); i++) { //여기 문제
            HashTagMap hashTagMap = new HashTagMap();
            hashTagMap.setPost(this);
            hashTagMap.setHashTag(new HashTag(postDto.getHash_tag().get(i)));
            this.addHashTagMaps(hashTagMap);
        }
    }

    public List<HashTag> getHashTag() {
        List<HashTag> array = new ArrayList<>();
        /*
        if (this.hash_tag != null) {
            for (int i = 0; i < this.hash_tag.size(); i++) {
                array.add(this.hash_tag.get(i));
            }
        }

         */
        return array;
    }


    public void addHashTagMaps(HashTagMap hashTagMap) {
        this.hashTagMaps.add(hashTagMap);
    }
}