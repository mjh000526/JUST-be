package com.example.just.Service;


import com.example.just.Dao.Member;
import com.example.just.Dao.Post;


import com.example.just.Dao.QPost;
import com.example.just.Dto.PostDto;
import com.example.just.Impl.MySliceImpl;
import com.example.just.Mapper.PostMapper;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;


import com.example.just.jwt.JwtProvider;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;


import java.util.*;
import java.util.stream.Collectors;

import static com.example.just.Dao.QPost.post;


@Service
@Slf4j
public class PostService {


    private final EntityManager em;

    private final JPAQueryFactory query;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private JwtProvider jwtProvider;
    public PostService(EntityManager em, JPAQueryFactory query) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }



    public Post write(Long member_id, PostDto postDto) {    //글 작성

        Optional<Member> optionalMember = memberRepository.findById(member_id);
        if (!optionalMember.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("DB에 존재하지 않는 ID : " + member_id);
        }
        Member member = optionalMember.get();   //존재한다면 객체 생성

        Long k = Long.valueOf((int)(Math.random()*3));

        Post post = new Post(postDto.getPost_content(), postDto.getPost_tag(),
                k, postDto.getSecret(), postDto.getEmoticon(), postDto.getPost_category(),0L,
                member,0);

        postRepository.save(post);

        return post;
    }


    //글 삭제
    public String deletePost(Long post_id) {

        try {
            postRepository.deleteById(post_id);
        }
        catch(Exception e) {
            throw new NoSuchElementException("post_id의 값이 DB에 존재하지 않습니다: "+ post_id);
        }
        return String.valueOf(post_id)+"번 게시글 삭제 완료";
    }

    //글 수정
    public Post putPost(Long post_id, Long member_id, PostDto postDto) {
        Optional<Post> optionalPost = postRepository.findById(post_id);
        if (!optionalPost.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("post_id의 값이 DB에 존재하지 않습니다:" + post_id);
        }
        Optional<Member> optionalMember = memberRepository.findById(member_id);
        if (!optionalMember.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("DB에 존재하지 않는 ID : " + member_id);
        }
        Member member = optionalMember.get();   //존재한다면 객체 생성
        postDto.setMember(member);
        Post post = postMapper.toEntity(postDto);
        postRepository.save(post);
        return post;
    }
    public Slice<Post> searchByCursor(String cursor, Long limit) { //글 조


        QPost post = QPost.post;
        Set<Long> viewedPostIds = new HashSet<>();

        // 이전에 본 글들의 ID를 가져옵니다.
        if (cursor != null) {
            String[] viewedPostIdsArray = cursor.split(",");
            viewedPostIds = new HashSet<>();
            for (String viewedPostId : viewedPostIdsArray) {
                viewedPostIds.add(Long.parseLong(viewedPostId));
            }
        }

        // 중복된 글을 제외하고 랜덤으로 limit+1개의 글을 가져옵니다.
        List<Post> results = query.selectFrom(post)
                .where(post.post_id.notIn(viewedPostIds),
                        post.post_create_time.isNotNull())
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit + 1)
                .fetch();

        // 가져온 글들의 ID를 저장합니다.
        Set<Long> resultPostIds = results.stream().map(Post::getPost_id).collect(Collectors.toSet());
        viewedPostIds.addAll(resultPostIds);

        // hasNext와 nextCursor를 계산합니다.
        boolean hasNext = results.size() > limit;
        String nextCursor = null;
        if (!viewedPostIds.isEmpty()) {
            nextCursor = String.join(",", viewedPostIds.stream().map(Object::toString).collect(Collectors.toList()));
        }

        // limit+1개의 글 중에서 limit개의 글만 남기고 제거합니다.
        if (hasNext) {
            results.remove(limit);
        }
        System.out.println("Slice.ofElem"+results.size());
        // Slice 객체를 생성해서 반환합니다.
        return new MySliceImpl<>(results, PageRequest.of(0, Math.toIntExact(limit)), hasNext, nextCursor);

    }
    @Transactional
    public Post postLikes(Long post_id, Long member_id) {    //글 좋아요


        Optional<Post> optionalPost = postRepository.findById(post_id);
        if (!optionalPost.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("post_id의 값이 DB에 존재하지 않습니다:" + post_id);
        }
        Post post = optionalPost.get();

        Optional<Member> optionalMember = memberRepository.findById(member_id);
        if (!optionalMember.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("DB에 존재하지 않는 ID : " + member_id);
        }
        Member member = optionalMember.get(); //존재한다면 객체 생성

        if (post.getLikedMembers().contains(member)) {
            post.removeLike(member);
        } else {
            post.addLike(member);
        }

        Post savePost= postRepository.save(post);
        return savePost;
    }




    public Slice<Post> searchByMyPost(Pageable pageable, HttpServletRequest request) {
        String token = request.getHeader("access_token");
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰

        List<Post> results = query.selectFrom(post)
                .where(
                        // no-offset 페이징 처리
                        // 이전 페이지의 마지막 아이템을 기준으로 조회
                        ltPostId(pageable.getOffset())
                        // 기타 조건들
                )
                .orderBy(post.post_id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        // 무한 스크롤 처리
        return checkLastPage(pageable, results);
    }

    // no-offset 방식 처리하는 메서드
    private BooleanExpression ltPostId(long offset) {
        if (offset <= 0) {
            return null;
        }
        // 이전 페이지의 마지막 아이템보다 작은 post_id를 가진 아이템 조회
        return post.post_id.lt(offset);
    }

    // 무한 스크롤 방식 처리하는 메서드
    private Slice<Post> checkLastPage(Pageable pageable, List<Post> results) {
        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }


}
