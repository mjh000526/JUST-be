package com.example.just.Service;


import com.example.just.Dao.HashTag;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;


import com.example.just.Dao.QPost;
import com.example.just.Document.PostDocument;
import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.example.just.Dto.ResponseGetMemberPostDto;
import com.example.just.Dto.ResponseGetPostDto;
import com.example.just.Dto.ResponsePutPostDto;
import com.example.just.Mapper.PostMapper;
import com.example.just.Repository.HashTagRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;

import com.example.just.jwt.JwtProvider;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
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
    private HashTagRepository hashTagRepository;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    PostContentESRespository postContentESRespository;

    public PostService(EntityManager em, JPAQueryFactory query) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    private Member checkMember(Long member_id) {
        Optional<Member> optionalMember = memberRepository.findById(member_id);
        if (!optionalMember.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("DB에 존재하지 않는 ID : " + member_id);
        }
        Member member = optionalMember.get();   //존재한다면 객체 생성
        return member;
    }

    private Post checkPost(Long post_id) {
        Optional<Post> optionalPost = postRepository.findById(post_id);
        if (!optionalPost.isPresent()) {  //아이디 없을시 예외처리
            throw new NoSuchElementException("post_id의 값이 DB에 존재하지 않습니다:" + post_id);
        }
        Post post = optionalPost.get();
        return post;
    }

    public PostPostDto write(Long member_id, PostPostDto postDto) {    //글 작성
        Member member = checkMember(member_id);
        Post post = new Post();
        post.writePost(postDto, member);
        postContentESRespository.save(new PostDocument(post));
        postRepository.save(post);
        return postDto;
    }

    //글 삭제
    public ResponsePost deletePost(Long post_id) {
        Post post = checkPost(post_id);
        postContentESRespository.deleteById(post_id);
        postRepository.deleteById(post_id);
        ResponsePost responsePost;
        if (post == null) {
            responsePost = new ResponsePost(post_id, "글이 없습니다.");
        } else {
            postRepository.deleteById(post_id);
            responsePost = new ResponsePost(post_id, "삭제 완료");
        }
        return responsePost;
    }

    //글 수정
    public ResponsePutPostDto putPost(Long member_id, PutPostDto postDto) {
        Long post_id = postDto.getPost_id();

        Member member = checkMember(member_id);
        Post checkPost = checkPost(post_id);

        List<HashTag> hashTags = hashTagRepository.findByPost(checkPost);
        for (int i = 0; i < hashTags.size(); i++) {
            hashTagRepository.deleteById(hashTags.get(i).getId());
        }
        checkPost.changePost(postDto, member, checkPost);
        postContentESRespository.save(new PostDocument(checkPost));
        postRepository.save(checkPost);
        ResponsePutPostDto responsePutPostDto = new ResponsePutPostDto(checkPost);
        return responsePutPostDto;
    }

    public List<Post> getAllPostList() {
        return postRepository.findAll();
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
                .limit(limit)
                .fetch();

        List<ResponseGetPostDto> getPostDtos = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            ResponseGetPostDto responseGetPostDto = new ResponseGetPostDto();
            responseGetPostDto.setPost_id(results.get(i).getPost_id());
            responseGetPostDto.setPost_content(results.get(i).getPostContent());
            responseGetPostDto.setPost_picture(results.get(i).getPost_picture());
            List<String> names = new ArrayList<>();
            List<HashTag> hashTags = results.get(i).getHash_tag();
            for (int j = 0; j < hashTags.size(); j++) {
                names.add(hashTags.get(j).getName());
            }
            responseGetPostDto.setHash_tag(names);
            responseGetPostDto.setPost_create_time(results.get(i).getPost_create_time());
            responseGetPostDto.setBlamed_count(Math.toIntExact(results.get(i).getBlamedCount()));
            responseGetPostDto.setSecret(results.get(i).getSecret());
            responseGetPostDto.setPost_like_size(results.get(i).getPost_like());
            responseGetPostDto.setComment_size((long) results.get(i).getComments().size());
            getPostDtos.add(responseGetPostDto);
            System.out.println(responseGetPostDto);
        }

        // 가져온 글들의 ID를 저장합니다.
        Set<Long> resultPostIds = results.stream().map(Post::getPost_id).collect(Collectors.toSet());
        viewedPostIds.addAll(resultPostIds);
        Collection<Post> allPost = postRepository.findAll();
        // hasNext와 nextCursor를 계산합니다.
        boolean hasNext = viewedPostIds.size() < allPost.size();

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
        return Math.toIntExact(post.getBlamedCount());

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


    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> postLikes(Long post_id, Long member_id) {    //글 좋아요

        Post post = checkPost(post_id);
        Member member = checkMember(member_id);

        ResponsePost responsePost;
        PostDocument postDocument = postContentESRespository.findById(post_id).get();
        if (post.getLikedMembers().contains(member)) {
            post.removeLike(member);
            postDocument.setPostLike(postDocument.getPostLike()-1);
            responsePost = new ResponsePost(post_id, "좋아요 취소");
        } else {
            post.addLike(member);
            postDocument.setPostLike(postDocument.getPostLike()+1);
            responsePost = new ResponsePost(post_id, "좋아요 완료");
        }

        postContentESRespository.save(postDocument);
        Post savePost = postRepository.save(post);

        return ResponseEntity.ok(responsePost);
    }


    public ResponseGetPost searchByCursorMember(String cursor, Long limit, Long member_id) {
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
                .limit(limit)
                .fetch();
        Optional<Member> member = memberRepository.findById(member_id);
        Member realMember = member.get();
        List<ResponseGetMemberPostDto> getPostDtos = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            ResponseGetMemberPostDto responseGetPostDto = new ResponseGetMemberPostDto();
            responseGetPostDto.setLike(false);
            for (int j = 0; j < results.get(i).getLikedMembers().size(); j++) {
                if (results.get(i).getLikedMembers().get(j).getId() == member_id) {
                    responseGetPostDto.setLike(true);
                    break;
                }
            }
            responseGetPostDto.setPost_id(results.get(i).getPost_id());
            responseGetPostDto.setPost_content(results.get(i).getPostContent());
            responseGetPostDto.setPost_picture(results.get(i).getPost_picture());
            List<String> names = new ArrayList<>();
            List<HashTag> hashTags = results.get(i).getHash_tag();
            for (int j = 0; j < hashTags.size(); j++) {
                names.add(hashTags.get(j).getName());
            }
            responseGetPostDto.setHash_tag(names);
            responseGetPostDto.setPost_create_time(results.get(i).getPost_create_time());
            responseGetPostDto.setBlamed_count(Math.toIntExact(results.get(i).getBlamedCount()));
            responseGetPostDto.setSecret(results.get(i).getSecret());
            responseGetPostDto.setPost_like_size(results.get(i).getPost_like());
            responseGetPostDto.setComment_size((long) results.get(i).getComments().size());
            getPostDtos.add(responseGetPostDto);
            System.out.println(responseGetPostDto);
        }

        // 가져온 글들의 ID를 저장합니다.
        Set<Long> resultPostIds = results.stream().map(Post::getPost_id).collect(Collectors.toSet());
        viewedPostIds.addAll(resultPostIds);
        Collection<Post> allPost = postRepository.findAll();
        // hasNext와 nextCursor를 계산합니다.
        boolean hasNext = viewedPostIds.size() < allPost.size();

        // Slice 객체를 생성해서 반환합니다.
        ResponseGetPost responseGetPost = new ResponseGetPost(
                getPostDtos, hasNext);
        return responseGetPost;
    }

    public List<ResponseGetMemberPostDto> getMyPost(Long member_id) {
        Optional<Member> member = memberRepository.findById(member_id);
        Member realMember = member.get();

        List<Post> results = realMember.getPosts();
        List<ResponseGetMemberPostDto> getPostDtos = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            ResponseGetMemberPostDto responseGetPostDto = new ResponseGetMemberPostDto();
            responseGetPostDto.setLike(false);
            for (int j = 0; j < results.get(i).getLikedMembers().size(); j++) {
                if (results.get(i).getLikedMembers().get(j).getId() == member_id) {
                    responseGetPostDto.setLike(true);
                    break;
                }
            }
            responseGetPostDto.setPost_id(results.get(i).getPost_id());
            responseGetPostDto.setPost_content(results.get(i).getPostContent());
            responseGetPostDto.setPost_picture(results.get(i).getPost_picture());
            List<String> names = new ArrayList<>();
            List<HashTag> hashTags = results.get(i).getHash_tag();
            for (int j = 0; j < hashTags.size(); j++) {
                names.add(hashTags.get(j).getName());
            }
            responseGetPostDto.setHash_tag(names);
            responseGetPostDto.setPost_create_time(results.get(i).getPost_create_time());
            responseGetPostDto.setBlamed_count(Math.toIntExact(results.get(i).getBlamedCount()));
            responseGetPostDto.setSecret(results.get(i).getSecret());
            responseGetPostDto.setPost_like_size(results.get(i).getPost_like());
            responseGetPostDto.setComment_size((long) results.get(i).getComments().size());
            getPostDtos.add(responseGetPostDto);
        }
        return getPostDtos;
    }

    public List<ResponseGetMemberPostDto> getLikeMemberPost(Long member_id) {
        Member member = checkMember(member_id); //존재한다면 객체 생성
        List<Post> results = member.getLikedPosts();
        List<ResponseGetMemberPostDto> getPostDtos = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            ResponseGetMemberPostDto responseGetPostDto = new ResponseGetMemberPostDto();
            responseGetPostDto.setLike(false);
            for (int j = 0; j < results.get(i).getLikedMembers().size(); j++) {
                if (results.get(i).getLikedMembers().get(j).getId() == member_id) {
                    responseGetPostDto.setLike(true);
                    break;
                }
            }
            responseGetPostDto.setPost_id(results.get(i).getPost_id());
            responseGetPostDto.setPost_content(results.get(i).getPostContent());
            responseGetPostDto.setPost_picture(results.get(i).getPost_picture());
            List<String> names = new ArrayList<>();
            List<HashTag> hashTags = results.get(i).getHash_tag();
            for (int j = 0; j < hashTags.size(); j++) {
                names.add(hashTags.get(j).getName());
            }
            responseGetPostDto.setHash_tag(names);
            responseGetPostDto.setPost_create_time(results.get(i).getPost_create_time());
            responseGetPostDto.setBlamed_count(Math.toIntExact(results.get(i).getBlamedCount()));
            responseGetPostDto.setSecret(results.get(i).getSecret());
            responseGetPostDto.setPost_like_size(results.get(i).getPost_like());
            responseGetPostDto.setComment_size((long) results.get(i).getComments().size());
            getPostDtos.add(responseGetPostDto);
        }
        return getPostDtos;
    }
}