package com.example.just.Document;

import com.example.just.Dao.Member;
import java.util.Date;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
public class NotificationDocument {

    @Id
    private Long notId;

    @Field(type = FieldType.Keyword)
    private String notType;

    @Field(type = FieldType.Long)
    private Long notPostId;

    @Field(type = FieldType.Date, format= DateFormat.basic_date)
    private Date notDatetime;

    @Field(type = FieldType.Boolean)
    private Boolean notIsRead; //알림 읽음 여부

    @Field(type = FieldType.Object)
    private Member receiver;

    @Field(type = FieldType.Long)
    private Long senderId;
}
