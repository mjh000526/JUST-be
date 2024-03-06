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
    date = "2024-03-06T13:28:25+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.11 (AdoptOpenJDK)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member toEntity(MemberDto arg0) {
        if ( arg0 == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.id( arg0.getId() );
        member.email( arg0.getEmail() );
        member.provider( arg0.getProvider() );
        member.provider_id( arg0.getProvider_id() );
        List<Post> list = arg0.getPosts();
        if ( list != null ) {
            member.posts( new ArrayList<Post>( list ) );
        }

        return member.build();
    }

    @Override
    public MemberDto toDto(Member arg0) {
        if ( arg0 == null ) {
            return null;
        }

        MemberDto.MemberDtoBuilder memberDto = MemberDto.builder();

        memberDto.id( arg0.getId() );
        memberDto.email( arg0.getEmail() );
        memberDto.provider( arg0.getProvider() );
        memberDto.provider_id( arg0.getProvider_id() );
        List<Post> list = arg0.getPosts();
        if ( list != null ) {
            memberDto.posts( new ArrayList<Post>( list ) );
        }

        return memberDto.build();
    }
}
