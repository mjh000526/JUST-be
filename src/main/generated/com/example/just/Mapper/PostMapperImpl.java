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
    date = "2024-03-06T13:28:25+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.11 (AdoptOpenJDK)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Override
    public Post toEntity(PutPostDto arg0) {
        if ( arg0 == null ) {
            return null;
        }

        Post.PostBuilder post = Post.builder();

        post.post_id( arg0.getPost_id() );
        post.post_picture( arg0.getPost_picture() );
        post.post_create_time( arg0.getPost_create_time() );
        post.secret( arg0.getSecret() );
        post.member( arg0.getMember() );
        List<Comment> list = arg0.getComments();
        if ( list != null ) {
            post.comments( new ArrayList<Comment>( list ) );
        }

        return post.build();
    }

    @Override
    public PutPostDto toDto(Post arg0) {
        if ( arg0 == null ) {
            return null;
        }

        PutPostDto putPostDto = new PutPostDto();

        putPostDto.setPost_id( arg0.getPost_id() );
        putPostDto.setPost_picture( arg0.getPost_picture() );
        putPostDto.setPost_create_time( arg0.getPost_create_time() );
        putPostDto.setSecret( arg0.getSecret() );
        putPostDto.setMember( arg0.getMember() );
        List<Comment> list = arg0.getComments();
        if ( list != null ) {
            putPostDto.setComments( new ArrayList<Comment>( list ) );
        }

        return putPostDto;
    }
}
