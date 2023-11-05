package com.example.just.Dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comment")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comment_id;    //댓글 아이디

    @Column(name = "comment_content")
    private String comment_content; //댓글 내용

    @Column(name = "comment_create_time")
    private Date comment_create_time;   //댓글 작성 시간

    @Column(name = "comment_like")
    private Long comment_like;  //추천수

    @Column(name = "comment_dislike")
    private Long comment_dislike;   //비추천수

    @ManyToOne
    @JoinColumn(name = "member_id") //댓글을 쓴 Member_id
    @JsonIgnore
    private Member member;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false) //해당 댓글이 달린 게시물
    @JsonIgnore
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_id") //해당 댓글의 부모 댓글
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    @JsonIgnore
    private List<Comment> children = new ArrayList<>(); //해당 댓글의 자식 댓글들

    @Column(name = "blamed_count")
    private int blamedCount;
    @ManyToMany(mappedBy = "likedComments")
    @JsonIgnore
    private List<Member> likedMembers = new ArrayList<>();
    public void addBlamed(){
        blamedCount++;
    }
    public void addLike(Member member) {
        if (!likedMembers.contains(member)) {
            System.out.println("멤버가 존재하지 않음 ");
            member.getLikedComments().add(this);//좋아한 글 List에 해당 글의 객체 추가
            comment_like++;
        }
    }

    public void removeLike(Member member) {
        if (likedMembers.contains(member)) {
            member.getLikedComments().remove(this);
            comment_like--;
        }
    }
}
