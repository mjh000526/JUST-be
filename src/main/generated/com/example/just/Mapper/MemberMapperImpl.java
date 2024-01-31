package com.example.just.Mapper;

import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dto.MemberDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-01-31T13:47:54+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.2.jar, environment: Java 11.0.11 (AdoptOpenJDK)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member toEntity(MemberDto dto) {
        if ( dto == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.id( dto.getId() );
        member.email( dto.getEmail() );
        member.provider( dto.getProvider() );
        member.provider_id( dto.getProvider_id() );
        List<Post> list = dto.getPosts();
        if ( list != null ) {
            member.posts( new ArrayList<Post>( list ) );
        }

        return member.build();
    }

    @Override
    public MemberDto toDto(Member entity) {
        if ( entity == null ) {
            return null;
        }

        MemberDto.MemberDtoBuilder memberDto = MemberDto.builder();

        memberDto.id( entity.getId() );
        memberDto.email( entity.getEmail() );
        memberDto.provider( entity.getProvider() );
        memberDto.provider_id( entity.getProvider_id() );
        List<Post> list = entity.getPosts();
        if ( list != null ) {
            memberDto.posts( new ArrayList<Post>( list ) );
        }

        return memberDto.build();
    }
}
