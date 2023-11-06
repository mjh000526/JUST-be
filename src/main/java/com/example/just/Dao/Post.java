package com.example.just.Dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@NoArgsConstructor
@Getter
@AllArgsConstructor
@Data
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    @ElementCollection
    @CollectionTable(name = "post_content", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "content")
    private List<String> postContent;

    @Column(name = "post_tag")  //글 태그
    private String post_tag;

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

    @Column(name = "post_category")
    private String post_category;

    @ManyToMany(mappedBy = "likedPosts", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Member> likedMembers = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(name = "member_id") //글을쓴 Member_id
    @JsonIgnore
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;
    @Column(name = "blamed_count")
    private int blamedCount;


    @PrePersist
    public void prePersist(){
        this.post_like = this.post_like == null ? 0L : this.post_like;
        this.post_category=this.post_category==null ? "0" : this.post_category;
        this.emoticon=this.emoticon==null? "0" : this.emoticon;

    }

    public Post(String post_tag, Long post_picture, boolean secret, String emoticon,
                String post_category, Long post_like, Member member, int blamedCount) {
        this.post_tag = post_tag;
        this.post_picture = post_picture;
        this.secret = secret;
        this.emoticon = emoticon;
        this.post_category = post_category;
        this.member = member;
        this.blamedCount = blamedCount;
        this.member.updateMember(this);
    }



    public void updatePost(String post_tag, Long post_like, Date post_create_time,
                           boolean secret, String emoticon, String post_category, Member member) {
        this.post_tag = post_tag;
        this.post_like = post_like;
        this.post_create_time = post_create_time;
        this.secret = secret;
        this.emoticon = emoticon;
        this.post_category = post_category;
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
    public void addBlamed(){
        blamedCount++;
    }


    public boolean getSecret() {
        return this.secret;
    }

}
