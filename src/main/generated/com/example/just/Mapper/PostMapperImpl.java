package com.example.just.Mapper;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dto.PostDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-02T21:34:12+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.2.jar, environment: Java 11.0.11 (AdoptOpenJDK)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Override
    public Post toEntity(PostDto dto) {
        if ( dto == null ) {
            return null;
        }

        Post post = new Post();

        post.setPost_id( dto.getPost_id() );
        post.setPost_content( dto.getPost_content() );
        post.setPost_tag( dto.getPost_tag() );
        post.setPost_picture( dto.getPost_picture() );
        post.setPost_create_time( dto.getPost_create_time() );
        post.setPost_like( dto.getPost_like() );
        post.setSecret( dto.getSecret() );
        post.setEmoticon( dto.getEmoticon() );
        post.setPost_category( dto.getPost_category() );
        List<Member> list = dto.getLikedMembers();
        if ( list != null ) {
            post.setLikedMembers( new ArrayList<Member>( list ) );
        }
        post.setMember( dto.getMember() );
        List<Comment> list1 = dto.getComments();
        if ( list1 != null ) {
            post.setComments( new ArrayList<Comment>( list1 ) );
        }
        post.setBlamedCount( dto.getBlamedCount() );

        return post;
    }

    @Override
    public PostDto toDto(Post entity) {
        if ( entity == null ) {
            return null;
        }

        PostDto postDto = new PostDto();

        postDto.setPost_id( entity.getPost_id() );
        postDto.setPost_content( entity.getPost_content() );
        postDto.setPost_tag( entity.getPost_tag() );
        postDto.setPost_like( entity.getPost_like() );
        postDto.setPost_picture( entity.getPost_picture() );
        postDto.setPost_create_time( entity.getPost_create_time() );
        postDto.setSecret( entity.getSecret() );
        postDto.setEmoticon( entity.getEmoticon() );
        postDto.setPost_category( entity.getPost_category() );
        List<Member> list = entity.getLikedMembers();
        if ( list != null ) {
            postDto.setLikedMembers( new ArrayList<Member>( list ) );
        }
        postDto.setMember( entity.getMember() );
        List<Comment> list1 = entity.getComments();
        if ( list1 != null ) {
            postDto.setComments( new ArrayList<Comment>( list1 ) );
        }
        postDto.setBlamedCount( entity.getBlamedCount() );

        return postDto;
    }
}
