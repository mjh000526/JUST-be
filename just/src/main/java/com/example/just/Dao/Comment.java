package com.example.just.Dao;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_id;    //댓글 아이디

    @Column(name = "comment_content")
    private String comment_content; //댓글 내용

    @Column(name = "comment_create_time")
    private Date comment_create_time;   //댓글 작성 시간

    @Column(name = "comment_reply")
    private String comment_reply;   //응답여부

    @Column(name = "comment_like")
    private Long comment_like;  //추천수

    @Column(name = "comment_dislike")
    private Long comment_dislike;   //비추천수

    @Column(name = "member_id")
    private Long member_id;

    @ManyToOne
    @JoinColumn(name = "post_id") //글을쓴 Member_id
    private Member member;
}
