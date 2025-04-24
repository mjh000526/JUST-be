package com.example.just.Document;

import com.example.just.Dao.HashTag;
import com.example.just.Dao.Post;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.format.annotation.DateTimeFormat;

@Document(indexName = "posts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Mapping(mappingPath = "elastic/post-mapping.json")
@Setting(settingPath = "elastic/post-setting.json")
public class PostDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private List<String> post_content;

    @Field(type = FieldType.Text)
    @Column(name = "hash_tag")
    private List<String> hashTag;

    @Field(type = FieldType.Long)
    private Long post_picture;

    @Field(type = FieldType.Long)
    private Date post_create_time;

    @Field(type = FieldType.Boolean)
    private Boolean secret;

    @Field(type = FieldType.Long)
    private Long comment_size;

    @Field(type = FieldType.Long)
    private Long post_like_size;

    @Field(type = FieldType.Long)
    private Long blamed_count;

    @Field(type = FieldType.Long)
    private Long member_id;



//    //글 태그
//    @Field(name = "hash_tag", type = FieldType.Nested)
//    private List<HashTag> hash_tag;
//
//
//    @Field(type = FieldType.Nested)
//    private List<Member> likedMembers = new ArrayList<>();
//
//    @Field(type = FieldType.Object)
//    private Member member;
//
//    @Field(type = FieldType.Nested)
//    private List<Comment> comments;


    public PostDocument(Post post) {
        this.id = post.getPost_id();
        this.post_content = post.getContents();
        this.hashTag = post.getHashTagMaps().stream()
                .map(hashTagMap -> {
                    String tagName = hashTagMap.getHashTag().getName();
                    return tagName != null ? tagName : "empty";  // null이 아닌 경우에는 그대로, null인 경우 "empty"로 대체
                })
                .collect(Collectors.toList());
        this.post_picture = post.getPost_picture();
        this.post_create_time = post.getPost_create_time();
        this.secret = post.getSecret();
        this.comment_size = (long) post.getComments().size();
        this.post_like_size = post.getPost_like();
        this.blamed_count = post.getBlamedCount();
        this.member_id = post.getMember().getId();
//        this.hash_tag = post.getHash_tag();
//        this.likedMembers = post.getLikedMembers();
//        this.member = post.getMember();
//        this.comments = post.getComments();
    }
}