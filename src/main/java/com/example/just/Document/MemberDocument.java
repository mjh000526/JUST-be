package com.example.just.Document;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Notification;
import com.example.just.Dao.Post;
import com.example.just.Dao.Role;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
public class MemberDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String email;
    
    @Field(type = FieldType.Object)
    private Role authority;

    @Field(type = FieldType.Date, format= DateFormat.basic_date)
    private Date createTime;

    @Field(type = FieldType.Keyword)
    private String provider;

    @Field(type = FieldType.Keyword)
    private String provider_id;

    @Field(type = FieldType.Keyword)
    private String nickname;

    //신고받은횟수
    @Field(type = FieldType.Integer)
    private int blamedCount;
    //신고한 횟수
    @Field(type = FieldType.Integer)
    private int blameCount;

    @Field(type = FieldType.Keyword)
    private String refreshToken;

    @Field(name = "posts")
    private List<Post> posts = new ArrayList<>();

    @Field(type = FieldType.Nested)
    private List<Comment> comments = new ArrayList<>();

    @Field(type = FieldType.Nested)
    private List<Post> likedPosts = new ArrayList<>();

    @Field(type = FieldType.Nested)
    private List<Comment> likedComments = new ArrayList<>();

    @Field(type = FieldType.Nested)
    private List<Notification> notifications;
}


