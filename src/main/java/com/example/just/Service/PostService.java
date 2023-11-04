package com.example.just.Service;

import com.example.just.Dao.Member;
import com.example.just.Dao.Post;


import com.example.just.Dao.QPost;
import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.example.just.Dto.ResponseGetPostDto;
import com.example.just.Dto.ResponsePutPostDto;
import com.example.just.Impl.MySliceImpl;
import com.example.just.Mapper.PostMapper;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;

import com.example.just.jwt.JwtProvider;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


@Service
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


    public PostPostDto write(Long member_id, PostPostDto postDto) {    //글 작성

        Optional<Member> optionalMember = memberRepository.findById(member_id);
        if (!optionalMember.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("DB에 존재하지 않는 ID : " + member_id);
        }
        Member member = optionalMember.get();   //존재한다면 객체 생성

        List<String> contentList = postDto.getPost_content();

        Post post = new Post(postDto.getPost_tag(),
                postDto.getPost_picture(), postDto.getSecret(), "", postDto.getPost_category(), 0L,
                member, 0);
        post.setPostContent(contentList);
        postRepository.save(post);

        return postDto;
    }


    //글 삭제
    @Transactional
    public ResponsePost deletePost(Long post_id) {
        Optional<Post> optionalPost = postRepository.findById(post_id);
        if (!optionalPost.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("post_id의 값이 DB에 존재하지 않습니다:" + post_id);
        }
        Post post = optionalPost.get();
        try {
            postRepository.delete(post);
        } catch (Exception e) {
            throw new NoSuchElementException("post_id의 값이 DB에 존재하지 않습니다: " + post_id);
        }
        ResponsePost responsePost = new ResponsePost(post_id, true);
        return responsePost;
    }

    //글 수정
    public ResponsePutPostDto putPost(Long member_id, PutPostDto postDto) {
        Long post_id = postDto.getPost_id();
        System.out.println(post_id);
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
        post.setPost_create_time(new Date(System.currentTimeMillis()));
        post.setPost_like(optionalPost.get().getPost_like());
        postRepository.save(post);
        ResponsePutPostDto responsePutPostDto = new ResponsePutPostDto(post.getPost_category(), post.getPostContent(),
                post_id, post.getPost_picture(), post.getPost_tag(), post.getSecret());
        return responsePutPostDto;
    }

    public ResponseGetPost searchByCursor(String cursor, Long limit) { //글 조
        QPost post = QPost.post;
        Set<Long> viewedPostIds = new HashSet<>();
        // 이전에 본 글들의 ID를 가져옵니다.
        if (cursor != null) {
            String[] viewedPostIdsArray = cursor.split(",");
            viewedPostIds = new HashSet<>();
            for (String viewedPostId : viewedPostIdsArray) {
                viewedPostIds.add(Long.parseLong(viewedPostId.trim()));
            }
        }

        // 중복된 글을 제외하고 랜덤으로 limit+1개의 글을 가져옵니다.
        List<Post> results = query.selectFrom(post)
                .where(post.post_id.notIn(viewedPostIds),
                        post.post_create_time.isNotNull())
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit + 1)
                .fetch();
        List<ResponseGetPostDto> getPostDtos = new ArrayList<>();
        for (int i=0; i<results.size(); i++) {
            ResponseGetPostDto responseGetPostDto = new ResponseGetPostDto();
            responseGetPostDto.setPost_id(results.get(i).getPost_id());
            responseGetPostDto.setPost_category(results.get(i).getPost_category());
            responseGetPostDto.setPost_picture(results.get(i).getPost_picture());
            responseGetPostDto.setPost_tag(results.get(i).getPost_tag());
            responseGetPostDto.setPost_create_time(results.get(i).getPost_create_time());
            responseGetPostDto.setBlamed_count(results.get(i).getBlamedCount());
            responseGetPostDto.setSecret(results.get(i).getSecret());
            responseGetPostDto.setComment_size((long) results.get(i).getComments().size());
            getPostDtos.add(responseGetPostDto);
            System.out.println(responseGetPostDto);
        }

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
        // Slice 객체를 생성해서 반환합니다.
        ResponseGetPost responseGetPost = new ResponseGetPost(
                getPostDtos, hasNext);
        return responseGetPost;

    }

    public Long blamePost(Long post_id) {
        Optional<Post> optionalPost = postRepository.findById(post_id);
        if (!optionalPost.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("post_id의 값이 DB에 존재하지 않습니다:" + post_id);
        }
        Post post = optionalPost.get();

        post.setBlamedCount(post.getBlamedCount() + 1);
        postRepository.save(post);
        return post_id;
    }

    public int blameGetPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (!optionalPost.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("post_id의 값이 DB에 존재하지 않습니다:" + postId);
        }
        Post post = optionalPost.get();
        return post.getBlamedCount();

    }
    /*
    public Slice<Post> searchByMyPost(Long limit, Long member_id) {


        List<Post> results = query.selectFrom(post)
                .where(
                        post.member.id.eq(member_id)
                )
                .orderBy(post.post_id.desc())
                .limit(limit + 1)
                .fetch();





        // hasNext와 nextCursor를 계산합니다.
        boolean hasNext = results.size() > limit;


        // limit+1개의 글 중에서 limit개의 글만 남기고 제거합니다.
        if (hasNext) {
            results.remove(limit);
        }


        return new SliceImpl<>(results, limit, hasNext);
    }

     */


    @Transactional
    public ResponsePost postLikes(Long post_id, Long member_id) {    //글 좋아요

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
        ResponsePost responsePost;
        if (post.getLikedMembers().contains(member)) {
            post.removeLike(member);
            responsePost = new ResponsePost(post_id, false);
        } else {
            post.addLike(member);
            responsePost = new ResponsePost(post_id, true);
        }

        Post savePost = postRepository.save(post);

        return responsePost;
    }


}