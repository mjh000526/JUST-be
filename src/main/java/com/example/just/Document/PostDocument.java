package com.example.just.Document;

import com.example.just.Dao.Post;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "items")
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

    @Field(type = FieldType.Nested)
    private List<ContentArray> postContent;

    @Field(type = FieldType.Long)
    private Long postPicture;

    @Field(type = FieldType.Long)
    private Date postCreateTime;

    @Field(type = FieldType.Long)
    private Long postLike;

    @Field(type = FieldType.Text)
    private String emoticon;

    @Field(type = FieldType.Long)
    private Long blamedCount;

    @Field(type = FieldType.Text)
    private String memberNickname;

    @Field(type = FieldType.Integer)
    private Integer commentCount;

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
        this.postContent = post.getPostContent().stream()
                .map(ContentArray::new)
                .collect(Collectors.toList());
        this.postPicture = post.getPost_picture();
        this.postCreateTime = post.getPost_create_time();
        this.postLike = post.getPost_like();
        this.emoticon = post.getEmoticon();
        this.blamedCount = post.getBlamedCount();
        this.memberNickname = post.getMember().getNickname();
        this.commentCount = post.getComments().size();

//        this.hash_tag = post.getHash_tag();
//        this.likedMembers = post.getLikedMembers();
//        this.member = post.getMember();
//        this.comments = post.getComments();
    }
}
