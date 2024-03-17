package com.example.just.Document;

import java.util.Date;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
public class BlameDocument {
    @Id
    private Long blameId;
    @Field(type = FieldType.Long)
    private Long targetId; //신고한 게시물 or 댓글 (id)
    @Field(type = FieldType.Text)
    private String targetType; //신고 분류
    @Field(type = FieldType.Date, format= DateFormat.basic_date)
    private Date blameDatetime; //신고를한 일시
    @Field(type = FieldType.Long)
    private Long blameMemberId; //신고를 한 회원
    @Field(type = FieldType.Long)
    private Long targetMemberId; //신고를 당한 회원
}