package com.example.just.Dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "post")
@NoArgsConstructor
@Getter
@Data
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    @Column(name = "post_content")  //글 내용
    private String post_content;

    @Column(name = "post_tag")  //글 태그
    private String post_tag;

    @Column(name = "post_like") //공감 회수
    private Long post_like;

    @Column(name = "post_create_time")  //글 생성 시간
    private LocalDateTime post_create_time;

    @Column(name = "secret")    //글 공개 여부
    private boolean secret;

    @Column(name = "emoticon")  //글 이모티콘
    private String emoticon;

    @Column(name = "post_category", nullable = true) //글 카테고리
    private Long post_category;

    @ManyToOne
    @JoinColumn(name = "member_id") //글을쓴 Member_id
    @JsonIgnore
    private Member member;

    public Post(String post_content, String post_tag, Long post_like, LocalDateTime post_create_time,
                boolean secret, String emoticon, Long post_category, Member member) {
        this.post_content = post_content;
        this.post_tag = post_tag;
        this.post_like = post_like;
        this.post_create_time = post_create_time;
        this.secret = secret;
        this.emoticon = emoticon;
        this.post_category = post_category;
        this.member = member;
        this.member.updateMember(this);
    }

    public void updatePost(String post_content, String post_tag, Long post_like, LocalDateTime post_create_time,
                boolean secret, String emoticon, Long post_category, Member member) {
        this.post_content = post_content;
        this.post_tag = post_tag;
        this.post_like = post_like;
        this.post_create_time = post_create_time;
        this.secret = secret;
        this.emoticon = emoticon;
        this.post_category = post_category;
        this.member = member;
        this.member.updateMember(this);
    }

}
