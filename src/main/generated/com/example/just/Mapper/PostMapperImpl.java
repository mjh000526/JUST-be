package com.example.just.Mapper;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Post;
import com.example.just.Dto.PostDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-03-23T18:34:25+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.2.jar, environment: Java 11.0.15.1 (Oracle Corporation)"
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
        post.setPost_create_time( dto.getPost_create_time() );
        post.setPost_like( dto.getPost_like() );
        post.setSecret( dto.isSecret() );
        post.setEmoticon( dto.getEmoticon() );
        post.setPost_category( dto.getPost_category() );
        post.setMember( dto.getMember() );
        List<Comment> list = dto.getComments();
        if ( list != null ) {
            post.setComments( new ArrayList<Comment>( list ) );
        }

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
        postDto.setPost_create_time( entity.getPost_create_time() );
        postDto.setSecret( entity.isSecret() );
        postDto.setEmoticon( entity.getEmoticon() );
        postDto.setPost_category( entity.getPost_category() );
        postDto.setMember( entity.getMember() );
        List<Comment> list = entity.getComments();
        if ( list != null ) {
            postDto.setComments( new ArrayList<Comment>( list ) );
        }

        return postDto;
    }
}
