package com.example.just.Dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "notification")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Notification{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notId;

    @Column(name = "not_type")  //알림 타입
    private String notType;

    @Column(name = "not_post_id")   //알림 내용
    private Long notPostId;

    @Column(name = "not_datetime")  //알림 발생 시일
    private Date notDatetime;
//
//    @Column(name = "not_read_datetime") //알림 읽은 시간
//    private Date not_read_dateTime;

    @Column(nullable = false)
    private Boolean notIsRead; //알림 읽음 여부
    @ManyToOne
    @JoinColumn(name = "id")
    @OnDelete(action = OnDeleteAction.CASCADE) //알림을 받을 member
    private Member receiver;

    @Column(name = "not_sender_id")   //알림 내용
    private Long senderId;


}
