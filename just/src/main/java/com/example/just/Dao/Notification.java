package com.example.just.Dao;

import lombok.Getter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "notification")
@Getter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long not_id;

    @Column(name = "not_type")  //알림 타입
    private String not_type;

    @Column(name = "not_content")   //알림 내용
    private String not_content;

    @Column(name = "not_url") //알림 클릭시 이동 주소
    private String not_url;

    @Column(name = "not_datetime")  //알림 발생 시일
    private Date not_datetime;

    @Column(name = "not_read_datetime") //알림 읽은 시간
    private Date not_read_dateTime;

    @ManyToOne
    @JoinColumn(name = "post_id")   //알림이 발생한 글
    private Post post;


}
