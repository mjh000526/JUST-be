package com.example.just.Document;

import com.example.just.Dao.HashTag;
import com.example.just.Dao.HashTagMap;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "tags")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Mapping(mappingPath = "elastic/tag-mapping.json")
//@Setting(settingPath = "elastic/post-setting.json")
//ELK에 동기화할 태그 테이블
public class HashTagDocument implements Comparable<HashTagDocument>{


    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Long)
    private Long tagCount;

    public HashTagDocument(HashTag hashTag){
        id = hashTag.getId();
        name = hashTag.getName();
        tagCount = hashTag.getTagCount();
    }

    @Override
    public int compareTo(@NotNull HashTagDocument o) { //태그 정렬 메소드
        if(o.tagCount < tagCount) return 1;
        else if(o.tagCount > tagCount) return -1;
        else return 0;
    }
}
