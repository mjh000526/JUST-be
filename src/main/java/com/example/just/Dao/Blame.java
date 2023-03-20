package com.example.just.Dao;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "blame")
public class Blame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blame_id;

    @Column(name = "target_id")
    private Long target_id; //신고한 게시물 or 댓글 (id)

    @Column(name = "target_type")
    private int target_type; //신고 분류

    @Column(name = "blame_datetime")
    private Date blame_datetime; //신고를한 일시

    @Column(name = "blame_member_id")
    private Long blame_member_id; //신고를 한 회원

    @Column(name = "target_member_id")
    private Long target_member_id; //신고를 당한 회원

}
