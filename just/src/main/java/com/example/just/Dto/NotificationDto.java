package com.example.just.Dto;

import com.example.just.Dao.Member;
import com.example.just.Dao.Notification;
import com.example.just.Dao.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private String not_type;    //알림 타입

    private String not_content;    //알림 내용

    private Date not_datetime;    //알림 발생 시일

    private Member member;    //알림이 발생한 글

    public static NotificationDto create(Notification notification){
        return new NotificationDto(
                notification.getNot_type(),
                notification.getNot_content(),
                notification.getNot_datetime(),
                notification.getMember()
        );
    }
}
