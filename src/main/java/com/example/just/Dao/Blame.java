package com.example.just.Dao;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

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
    private Long blameId;

    @Column(name = "target_id")
    private Long targetId; //신고한 게시물 or 댓글 (id)

    @Column(name = "target_type")
    private String targetType; //신고 분류

    @Column(name = "blame_datetime")
    private Date blameDatetime; //신고를한 일시

    @Column(name = "blame_member_id")
    private Long blameMemberId; //신고를 한 회원

    @Column(name = "target_member_id")
    private Long targetMemberId; //신고를 당한 회원

}
