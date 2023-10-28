package com.example.just.Mapper;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dto.PutPostDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-28T16:47:57+0900",
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
        post.setPost_content( dto.getPost_content() );
        post.setPost_tag( dto.getPost_tag() );
        post.setPost_picture( dto.getPost_picture() );
        post.setPost_create_time( dto.getPost_create_time() );
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

        return post;
    }

    @Override
    public PutPostDto toDto(Post entity) {
        if ( entity == null ) {
            return null;
        }

        PutPostDto putPostDto = new PutPostDto();

        putPostDto.setPost_id( entity.getPost_id() );
        putPostDto.setPost_content( entity.getPost_content() );
        putPostDto.setPost_tag( entity.getPost_tag() );
        putPostDto.setPost_picture( entity.getPost_picture() );
        putPostDto.setPost_create_time( entity.getPost_create_time() );
        putPostDto.setSecret( entity.getSecret() );
        putPostDto.setEmoticon( entity.getEmoticon() );
        putPostDto.setPost_category( entity.getPost_category() );
        List<Member> list = entity.getLikedMembers();
        if ( list != null ) {
            putPostDto.setLikedMembers( new ArrayList<Member>( list ) );
        }
        putPostDto.setMember( entity.getMember() );
        List<Comment> list1 = entity.getComments();
        if ( list1 != null ) {
            putPostDto.setComments( new ArrayList<Comment>( list1 ) );
        }

        return putPostDto;
    }
}
