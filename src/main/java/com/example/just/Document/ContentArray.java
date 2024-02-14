package com.example.just.Document;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@Getter
public class ContentArray {
    @Field(type = FieldType.Text)
    String content;
    public ContentArray(){}
    public ContentArray(String content){
        this.content = content;
    }

    public ContentArray(ContentArray contentArray) {
        this.content = contentArray.getContent();
    }
}
