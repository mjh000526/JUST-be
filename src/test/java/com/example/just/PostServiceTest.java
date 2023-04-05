


/*package com.example.just;

import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dao.Role;
import com.example.just.Dto.PostDto;
import com.example.just.Impl.MySliceImpl;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
import com.example.just.Service.PostService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostServiceTest {



    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private JPAQueryFactory queryFactory;

    @Autowired
    private PostService postService;

    void setUp() {

        // given    //Test Member , Post 객체 생성
        Member member = new Member();
        member.setBlameCount(0);
        member.setBlamedCount(0);
        member.setEmail("slee000220@naver.com");
        member.setNickname("테스트");
        member.setProvider("kakao");
        member.setProvider_id("2729809374");
        member.setRole(Role.valueOf("ROLE_USER"));
        memberRepository.save(member);

        PostDto postDto = new PostDto();    //post생성
        postDto.setPost_content("test content");
        postDto.setPost_tag("#Test하는 중!");
        postDto.setSecret(true);

        Long k = Long.valueOf((int)(Math.random()*3));
        Post post = new Post(postDto.getPost_content(), postDto.getPost_tag(),
                k, postDto.getSecret(), postDto.getEmoticon(), postDto.getPost_category(),0L,
                member,0);
        postRepository.save(post);
    }


    @Test
    @DisplayName("글 작성")
    @Order(1)
    void 글_작성() throws InterruptedException {
        // given    //Test Member , Post 객체 생성
        if(memberRepository.findAll()!=null) {
            Member member = new Member();
            member.setId(1L);
            member.setBlameCount(0);
            member.setBlamedCount(0);
            member.setEmail("slee000220@naver.com");
            member.setNickname("테스트");
            member.setProvider("kakao");
            member.setProvider_id("2729809374");
            member.setRole(Role.valueOf("ROLE_USER"));
            memberRepository.save(member);
        }

        PostDto postDto = new PostDto();    //post생성
        postDto.setPost_content("test content");
        postDto.setPost_tag("#Test하는 중!");
        postDto.setSecret(true);


        // when 글쓰기 API호출
        Post post = postService.write(1L, postDto);

        // then 다 잘 저장됐는지 확인
        assertNotNull(post.getPost_id());
        assertEquals("test content", post.getPost_content());
        assertEquals("#Test하는 중!", post.getPost_tag());
        assertEquals(true,post.getSecret());
        assertEquals("0", post.getEmoticon());
        assertEquals("0", post.getPost_category());
        assertEquals(0L,post.getPost_like());
        assertEquals(0,post.getBlamedCount());
    }

    @Test
    @DisplayName("글 삭제")
    @Order(2)
    void 글_삭제() throws InterruptedException {
        // given
        Long post_id = 2L;
        setUp();

        // when 글쓰기 API호출
        String post = postService.deletePost(post_id);

        // then 다 잘 삭제되는지 확인
        assertEquals("2번 게시글 삭제 완료", post);
    }


    @Test
    @DisplayName("글 수정")
    @Order(3)
    void 글_수정() {
        //given
        setUp();
        Long post_id = 2L;
        Long member_id=2L;

        Optional<Member> optionalMember = memberRepository.findById(member_id);
        if (!optionalMember.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("DB에 존재하지 않는 ID : " + member_id);
        }
        Member member = new Member(optionalMember.get());   //존재한다면 객체 생성

        PostDto postDto = new PostDto();
        postDto.setPost_category("연애");
        postDto.setPost_create_time(new Timestamp(System.currentTimeMillis()));
        postDto.setPost_like(5L);
        postDto.setEmoticon("웃음");
        postDto.setPost_picture(1L);
        postDto.setPost_id(post_id);
        postDto.setSecret(false);
        postDto.setPost_tag("#테스트");
        postDto.setBlamedCount(200);
        postDto.setPost_content("파핫");
        postDto.setMember(member);

        //when
        Post post = postService.putPost(post_id, member_id, postDto);

        //then
        assertEquals("연애", post.getPost_category());
        assertEquals(5L, post.getPost_like());
        assertEquals(false, post.getSecret());
    }

    @Test
    @DisplayName("글 좋아요!")
    @Order(4)
    void 글_좋아요() {
        //given
        setUp();
        Long post_id = 1L;
        Long member_id = 1L;

        //when
        Post post = postService.postLikes(post_id, member_id); //좋아요 누르기내
        System.out.println(post.getPost_like());
        Post post2 = postService.postLikes(post_id, member_id);
        System.out.println(post.getPost_like());
        System.out.println(post.getLikedMembers().size());
        //then
        assertEquals(1L, post.getPost_like());
        assertEquals(0L, post2.getPost_like());


    }

    /*
    @Test
    @DisplayName("글 랜덤 조회")
    @Order(5)
    void 글_랜덤_조회() {
        //given
        setUp();
        setUp();
        setUp();
        setUp();
        Long request_page = 2L;
        String cursor = "1,3";

        //when
        MySliceImpl<Post> post = (MySliceImpl<Post>) postService.searchByCursor(cursor, request_page);

        //then
        System.out.println(post);
    }

     */

