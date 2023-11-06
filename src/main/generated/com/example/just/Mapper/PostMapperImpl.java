package com.example.just.Mapper;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Post;
import com.example.just.Dto.PutPostDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-11-06T18:15:08+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.2.jar, environment: Java 11.0.11 (AdoptOpenJDK)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Override
    public Post toEntity(PutPostDto dto) {
        if ( dto == null ) {
            return null;
        }

        Post post = new Post();

        post.setPost_id( dto.getPost_id() );
        post.setPost_tag( dto.getPost_tag() );
        post.setPost_picture( dto.getPost_picture() );
        post.setPost_create_time( dto.getPost_create_time() );
        post.setSecret( dto.getSecret() );
        post.setPost_category( dto.getPost_category() );
        post.setMember( dto.getMember() );
        List<Comment> list = dto.getComments();
        if ( list != null ) {
            post.setComments( new ArrayList<Comment>( list ) );
        }

        return post;
    }

    @Override
    public PutPostDto toDto(Post entity) {
        if ( entity == null ) {
            return null;
        }

        PutPostDto putPostDto = new PutPostDto();

        putPostDto.setPost_id( entity.getPost_id() );
        putPostDto.setPost_tag( entity.getPost_tag() );
        putPostDto.setPost_picture( entity.getPost_picture() );
        putPostDto.setPost_create_time( entity.getPost_create_time() );
        putPostDto.setSecret( entity.getSecret() );
        putPostDto.setPost_category( entity.getPost_category() );
        putPostDto.setMember( entity.getMember() );
        List<Comment> list = entity.getComments();
        if ( list != null ) {
            putPostDto.setComments( new ArrayList<Comment>( list ) );
        }

        return putPostDto;
    }
}
