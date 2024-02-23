package com.example.just.Dao;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "blame")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Blame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blame_id")
    private Long blameId;

    @Column(name = "target_index")
    private Long targetIndex; //신고내용 index

    @CreationTimestamp
    @Column(name = "blame_datetime")
    private Date blameDatetime; //신고를한 일시

    @Column(name = "blame_member_id")
    private Long blameMemberId; //신고를 한 회원

    @Column(name = "target_member_id")
    private Long targetMemberId; //신고를 당한 회원

    @Column(name = "target_post_id")
    private Long targetPostId; //신고를 한 회원

    @Column(name = "target_comment_id")
    private Long targetCommentId; //신고를 당한 회원

}
