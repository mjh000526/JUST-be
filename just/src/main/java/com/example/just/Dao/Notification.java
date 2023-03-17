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
    private Long not_id;

    @Column(name = "not_type")  //알림 타입
    private String not_type;

    @Column(name = "not_content")   //알림 내용
    private String not_content;

    @Column(name = "not_datetime")  //알림 발생 시일
    private Date not_datetime;

    @Column(name = "not_read_datetime") //알림 읽은 시간
    private Date not_read_dateTime;

    @Column(nullable = false)
    private Boolean not_isRead; //알림 읽음 여부
    @ManyToOne
    @JoinColumn(name = "id")
    @OnDelete(action = OnDeleteAction.CASCADE) //알림을 받을 member
    private Member member;


}
