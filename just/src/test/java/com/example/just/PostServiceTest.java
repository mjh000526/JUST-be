package com.example.just;


import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dao.QPost;
import com.example.just.Dto.PostDto;
import com.example.just.Impl.MySliceImpl;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
import com.example.just.Service.PostService;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Slice;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Transactional
public class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private MemberRepository memberRepository;
    @Mock
    private PostRepository postRepository;

    @Test
    public void writeTest() {
        // given
        Member member = new Member();
        member.setEmail("test@test.com");
        member.setId(1L);

        memberRepository.save(member);
        PostDto postDto = new PostDto("content", "tag", 0, LocalDateTime.now(),
                false, null, null);

        // when
        Post post = postService.write(member.getId(), postDto);

        // then
        assertNotNull(post);
        assertNotNull(post.getPost_id());
        assertEquals(postDto.getPost_content(), post.getPost_content());
        assertEquals(postDto.getPost_tag(), post.getPost_tag());
        assertEquals(postDto.getPost_like(), post.getPost_like());
        assertEquals(postDto.getPost_create_time(), post.getPost_create_time());
        assertEquals(postDto.isSecret(), post.isSecret());
        assertEquals(postDto.getEmoticon(), post.getEmoticon());
        assertEquals(postDto.getPost_category(), post.getPost_category());
        assertEquals(member.getId(), post.getMember().getId());
    }

    @Test
    public void searchByCursorTest() {
        // given
        Member member1 = new Member();
        member1.setEmail("test1@test.com");
        member1.setId(1L);
        Member member2 = new Member();
        member2.setEmail("test2@test.com");
        member2.setId(2L);

        memberRepository.saveAll(Arrays.asList(member1, member2));
        //맴버 저장

        PostDto postDto = new PostDto("content", "tag", 0, LocalDateTime.now(), false, null, null);
        Post post1 = postService.write(member1.getId(), postDto);
        Post post2 = postService.write(member1.getId(), postDto);
        Post post3 = postService.write(member1.getId(), postDto);
        Post post4 = postService.write(member2.getId(), postDto);
        //1번 회원글 3개 2번회원 글 1개 총 글 4개 생성

        // when
        Slice<Post> slice1 = postService.searchByCursor(null, 2L);
        //2개의 글을 보여줌 중복당연히 X
        Slice<Post> slice2 = postService.searchByCursor(((MySliceImpl<Post>) slice1).getNextCursor(), 2L);
        //slice1에서 보여준글들을 제외한 글들을 보여줌
        Slice<Post> slice3 = postService.searchByCursor(((MySliceImpl<Post>) slice1).getNextCursor()+
                ((MySliceImpl<Post>) slice2).getNextCursor(), 2L);
        //slice1에서 보여준글들을 제외한 글들을 보여줌

        // then
        System.out.println("slice1: " + slice1.getContent());
        assertEquals(3, slice1.getNumberOfElements());
        assertTrue(slice1.hasNext()); //다음글이있냐


        assertEquals(1, slice2.getNumberOfElements());
        assertFalse(slice2.hasNext()); //다음글이있냐


    }


}
