package com.example.just.Dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "notification")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification{ //DB 알림 정보 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notId;

    @Column(name = "not_object_id")   //알림 내용
    private Long notObjectId;

    @Column(name = "not_type")  //알림 종류
    private String notType;

    @Column(name = "not_datetime")  //알림 발생 시일
    @CreationTimestamp
    private Date notDatetime;

    @Column(nullable = false)   //알림 읽음 여부
    private Boolean notIsRead;

    @ManyToOne
    @JoinColumn(name = "id")
    @OnDelete(action = OnDeleteAction.CASCADE) //알림을 받을 member
    private Member receiver;

    @Column(name = "not_sender_id")   //송신자 id
    private Long senderId;


}
