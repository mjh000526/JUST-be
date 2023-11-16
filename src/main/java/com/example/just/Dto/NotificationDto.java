package com.example.just.Dto;

import com.example.just.Dao.Member;
import com.example.just.Dao.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto implements Serializable {

    private String not_type;    //알림 타입

    private Long not_post_id;    //알림 내용

    private Date not_datetime;    //알림 발생 시일

    private Long receiver;    //알림이 발생한 글

    private Long sender_id;      //알림 송신자

    public static NotificationDto create(Notification notification){
        return new NotificationDto(
                notification.getNotType(),
                notification.getNotPostId(),
                notification.getNotDatetime(),
                notification.getReceiver().getId(),
                notification.getSenderId()
        );
    }
}
