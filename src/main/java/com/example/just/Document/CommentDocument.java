package com.example.just.Document;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
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
public class CommentDocument {
    @Id
    private Long comment_id;    //댓글 아이디

    @Field(type = FieldType.Text)
    private String comment_content; //댓글 내용

    @Field(type = FieldType.Date, format= DateFormat.basic_date)
    private Date comment_create_time;   //댓글 작성 시간

    @Field(type = FieldType.Long)
    private Long comment_like;  //추천수

    @Field(type = FieldType.Long)
    private Long comment_dislike;   //비추천수

    @Field(type = FieldType.Object)
    private Member member;

    @Field(type = FieldType.Object)
    private Post post;

    @Field(type = FieldType.Object)
    private Comment parent;

    @Field(type = FieldType.Nested)
    private List<Comment> children = new ArrayList<>(); //해당 댓글의 자식 댓글들

    @Field(type = FieldType.Integer)
    private int blamedCount;

    @Field(type = FieldType.Nested)
    private List<Member> likedMembers = new ArrayList<>();
}
