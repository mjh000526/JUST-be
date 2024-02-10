package com.example.just.Dao;

import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
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
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    @ElementCollection
    @CollectionTable(name = "post_content", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "content")
    @JsonIgnore
    private List<String> postContent;

    //글 태그
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Column(name = "tag")
    private List<HashTag> hash_tag;

    @Column(name = "post_picture")
    private Long post_picture;

    @CreationTimestamp
    @Column(name = "post_create_time")  //글 생성 시간
    private Date post_create_time;

    @Column(name = "post_like")
    private Long post_like;

    @Column(name = "secret")
    private boolean secret;

    @Column(name = "emoticon")
    private String emoticon;

    @ManyToMany(mappedBy = "likedPosts", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Member> likedMembers = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(name = "member_id") //글을쓴 Member_id
    @JsonIgnore
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments;
    @Column(name = "blamed_count")
    private Long blamedCount;


    @PrePersist
    public void prePersist() {
        this.post_like = this.post_like == null ? 0L : this.post_like;
        this.emoticon = this.emoticon == null ? "0" : this.emoticon;
    }

    public void writePost(PostPostDto postDto, Member member) { // 글 쓰기 생성자
        List<String> contentList = postDto.getPost_content();
        this.postContent = contentList;
        for (int i = 0; i < postDto.getHash_tage().size(); i++) {
            String hashTag_name = postDto.getHash_tage().get(i);
            HashTag hashTag = new HashTag(hashTag_name);
            addHashTag(hashTag);
        }
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
        this.member.updateMember(this);
    }

    public void addLike(Member member) {
        if (!likedMembers.contains(member)) {
            System.out.println("멤버가 존재하지 않음 ");
            member.getLikedPosts().add(this);//좋아한 글 List에 해당 글의 객체 추가
            post_like++;
        }
    }

    public void removeLike(Member member) {
        if (likedMembers.contains(member)) {
            member.getLikedPosts().remove(this);
            post_like--;
        }
    }

    public void addHashTag(HashTag hashTag) {
        if (hash_tag == null) {
            hash_tag = new ArrayList<>();
        }
        hash_tag.add(hashTag);
        hashTag.setPost(this);
    }

    public void addBlamed() {
        blamedCount++;
    }


    public boolean getSecret() {
        return this.secret;
    }

    public void changePost(PutPostDto postDto, Member member, Post post) {
        this.post_id = post.getPost_id();
        this.member = member;
        this.setPost_create_time(new Date(System.currentTimeMillis()));
        this.setPost_like(post.getPost_like());
        this.post_picture = postDto.getPost_picture();
        this.secret = postDto.getSecret();
        this.postContent = postDto.getPost_content();
        this.hash_tag=null;
        for (int i = 0; i < postDto.getHash_tage().size(); i++) {
            String hashTag_name = postDto.getHash_tage().get(i);
            HashTag hashTag = new HashTag(hashTag_name);
            addHashTag(hashTag);
        }
    }

    public List<String> getHashTag() {
        List<String> array = new ArrayList<>();
        for (int i = 0; i < this.hash_tag.size(); i++) {
            array.add(this.hash_tag.get(i).getName());
        }
        return array;
    }
}