package com.example.just.Dto;

import javax.persistence.Column;
import java.util.Date;

public class BlameDto {
    private Long blame_id;

    private Long target_id; //신고한 게시물 or 댓글 (id)

    private int target_type; //신고 분류

    private Date blame_datetime; //신고를한 일시

    private Long blame_member_id; //신고를 한 회원

    private Long target_member_id; //신고를 당한 회원
}
