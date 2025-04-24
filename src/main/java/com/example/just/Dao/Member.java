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
  @Builder
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public class Member { //DB 회원 정보 테이블
      @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      private String email; //회원 이메일
      @Enumerated(EnumType.STRING)
      @Setter
      private Role authority;//회원 권한

      @CreationTimestamp
      @Column(name = "create_time")
      private Date createTime; //계정 생성일

      private String provider; //auth 제공사

      private String provider_id;//제공사 고유 id
      private String nickname;//닉네임

      @Column(name = "blamed_count")
      private int blamedCount;//신고당한 횟수

      @Column(name = "blame_count")
      private int blameCount; //신고한 횟수

      @Column(name = "refresh_token")
      private String refreshToken;//재발급 토큰

      @Builder.Default //안 써도 되는데 경고떠서 그냥 부침
      @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY,  orphanRemoval=true)
      @JsonIgnore
      private List<Post> posts = new ArrayList<>();//작성한 글 리스트

      @Builder.Default
      @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY,  orphanRemoval=true)
      @JsonIgnore
      private List<Comment> comments = new ArrayList<>();//작성한 댓글 리스트

      @Builder.Default
      @ManyToMany(mappedBy = "likedMembers", cascade = CascadeType.REMOVE)
      private List<Post> likedPosts = new ArrayList<>();//좋아요를 누른 게시글 리스트

      @Builder.Default
      @ManyToMany(mappedBy = "likedMembers", cascade = CascadeType.REMOVE)
      private List<Comment> likedComments = new ArrayList<>();//좋아요를 누른 댓글 리스트
      public void addBlamed(){
          blamedCount++;
      }

      public void removeBlame() {blameCount--;}
      public void addBlame(){
          blameCount++;
      }



      public Member(Member member) {
          this.id = member.getId();
          this.email = member.getEmail();
          this.authority = member.getAuthority();
          this.createTime = member.getCreateTime();
          this.provider = member.getProvider();
          this.provider_id = member.getProvider_id();
          this.nickname = member.getNickname();
          this.blamedCount = member.getBlamedCount();
          this.blameCount = member.getBlameCount();
          this.posts = member.getPosts();
          this.likedPosts = member.getLikedPosts();
      }
  }


