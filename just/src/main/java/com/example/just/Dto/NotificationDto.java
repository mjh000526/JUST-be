package com.example.just.Dto;

import com.example.just.Dao.Post;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

public class NotificationDto {

    private Long not_id;    //알림 아이디

    private String not_type;    //알림 타입

    private String not_content;    //알림 내용

    private String not_url;    //알림 클릭시 이동 주소

    private Date not_datetime;    //알림 발생 시일


    private Date not_read_dateTime;    //알림 읽은 시간


    private Post post;    //알림이 발생한 글
}
