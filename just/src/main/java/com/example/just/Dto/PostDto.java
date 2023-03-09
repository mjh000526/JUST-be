package com.example.just.Dto;

import com.example.just.Dao.Member;
import com.example.just.Dao.Notification;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PostDto {
    private Long post_id;

    private String post_content;    //글 내용

    private String post_tag;    //글 태그

    private Long post_like; //공감 회수

    private Date post_create_time;  //글 생성 시간

    private boolean secret; //글 공개 여부


    private String emoticon;     //글 이모티콘


    private Long Post_category; //글 카테고리


    private Member member;  //글을쓴 Member_id

    private List<Notification> notifications;     //글에 대한 알림

    public boolean isSecret() {
        return secret;
    }
}
