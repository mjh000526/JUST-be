package com.example.just.Service;


import com.example.just.Dao.HashTag;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;


import com.example.just.Dao.QBlame;
import com.example.just.Dao.QPost;
import com.example.just.Document.PostDocument;
import com.example.just.Dto.GptRequestDto;
import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.example.just.Repository.BlameRepository;
import com.example.just.Response.ResponseGetMemberPostDto;
import com.example.just.Response.ResponseGetPostDto;
import com.example.just.Response.ResponsePutPostDto;
import com.example.just.Mapper.PostMapper;
import com.example.just.Repository.HashTagRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;

import com.example.just.jwt.JwtProvider;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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
    private BlameRepository blameRepository;
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private GptService gptService;

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

    private Post checkPost(Long post_id) throws NotFoundException {
        Optional<Post> optionalPost = postRepository.findById(post_id);
        if (!optionalPost.isPresent()) {  //아이디 없을시 예외처리
            throw new NotFoundException();
        }
        Post post = optionalPost.get();
        return post;
    }

    public PostPostDto write(Long member_id, PostPostDto postDto) {    //글 작성
        Member member = checkMember(member_id);
        Post post = new Post();

        if (postDto.getHash_tag() == null) {
            String prompt = "";
            for (int i = 0; i < postDto.getPost_content().size(); i++) {
                prompt += postDto.getPost_content().get(i) + " ";
            }
            GptRequestDto gptRequestDto = new GptRequestDto(prompt);
            List<String> tag = gptService.getTag(gptRequestDto);
            postDto.setHash_tag(tag);
        }
        post.writePost(postDto, member);
        Post p = postRepository.save(post);
        postContentESRespository.save(new PostDocument(p));
        return postDto;
    }

    //글 삭제
    public void deletePost(Long post_id) throws NotFoundException {
        Post post = checkPost(post_id);
        ResponsePost responsePost;
        if (post == null) {
            throw new NotFoundException();
        } else {
            postContentESRespository.deleteById(post_id);
            postRepository.deleteById(post_id);
        }
    }

    //글 수정
    public ResponsePutPostDto putPost(Long member_id, PutPostDto postDto) throws NotFoundException {
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

    public ResponseGetPost searchByCursor(String cursor, Long limit, Long member_id) throws NotFoundException { //글 조
        QPost post = QPost.post;
        QBlame blame = QBlame.blame;
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
        if (results.size() == 0) {
            throw new NotFoundException();
        } else {
            List<ResponseGetMemberPostDto> getPostDtos = createResponseGetMemberPostDto(results, member_id);
            return resultPostIds(viewedPostIds, results, getPostDtos);
        }
    }

    private ResponseGetPost resultPostIds(Set<Long> viewedPostIds, List<Post> results,
                                          List<ResponseGetMemberPostDto> getPostDtos) {
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

    private List<ResponseGetMemberPostDto> createResponseGetMemberPostDto(List<Post> results, Long member_id) {
        List<ResponseGetMemberPostDto> getPostDtos = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            ResponseGetMemberPostDto responseGetMemberPostDto = new ResponseGetMemberPostDto(results, member_id, i);
            getPostDtos.add(responseGetMemberPostDto);
        }
        return getPostDtos;
    }

    public Long blamePost(Long post_id) throws NotFoundException {
        Post post = checkPost(post_id);
        post.setBlamedCount(post.getBlamedCount() + 1);
        postRepository.save(post);
        return post_id;
    }

    public int blameGetPost(Long postId) throws NotFoundException {
        Post post = checkPost(postId);
        return Math.toIntExact(post.getBlamedCount());

    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> postLikes(Long post_id, Long member_id) throws NotFoundException {    //글 좋아요

        Post post = checkPost(post_id);
        Member member = checkMember(member_id);

        ResponsePost responsePost;
        PostDocument postDocument = postContentESRespository.findById(post_id).get();
        if (post.getLikedMembers().contains(member)) {
            post.removeLike(member);
            postDocument.setPostLike(postDocument.getPostLike() - 1);
            responsePost = new ResponsePost(post_id, "좋아요 취소");
        } else {
            post.addLike(member);
            postDocument.setPostLike(postDocument.getPostLike() + 1);
            responsePost = new ResponsePost(post_id, "좋아요 완료");
        }

        postContentESRespository.save(postDocument);
        Post savePost = postRepository.save(post);

        return ResponseEntity.ok(responsePost);
    }


    public ResponseGetPost searchByCursorMember(String cursor, Long limit, Long member_id) throws NotFoundException {
        QPost post = QPost.post;
        QBlame blame = QBlame.blame;
        Set<Long> viewedPostIds = new HashSet<>();
        // 이전에 본 글들의 ID를 가져옵니다.
        if (cursor != null) {
            String[] viewedPostIdsArray = cursor.split(",");
            viewedPostIds = new HashSet<>();
            for (String viewedPostId : viewedPostIdsArray) {
                viewedPostIds.add(Long.parseLong(viewedPostId.trim()));
            }
        }

        Optional<Member> member = memberRepository.findById(member_id);
        Member realMember = member.get();

        List<Long> blames = query.select(blame.targetPostId)
                .from(blame)
                .where(blame.blameMemberId.eq(realMember.getId()))
                .fetch();
        List<Long> targetMembers = query.select(blame.targetMemberId)
                .from(blame)
                .where(blame.blameMemberId.eq(realMember.getId()))
                .fetch();

        System.out.println(blames);
        System.out.println(targetMembers);
        // 중복된 글을 제외하고 랜덤으로 limit+1개의 글을 가져옵니다.
        List<Post> results = query.select(post)
                .from(post)
                .where(post.post_id.notIn(viewedPostIds),
                        post.post_create_time.isNotNull(),
                        post.post_id.notIn(blames),
                        post.member.id.notIn(targetMembers))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit)
                .fetch();
        List<ResponseGetMemberPostDto> getPostDtos = new ArrayList<>();
        if (results.size() == 0) {
            throw new NotFoundException();
        } else {
            getPostDtos = createResponseGetMemberPostDto(results, member_id);
            // 가져온 글들의 ID를 저장합니다.
            return resultPostIds(viewedPostIds, results, getPostDtos);
        }
    }

    public List<ResponseGetMemberPostDto> getMyPost(Long member_id) throws NotFoundException {
        Optional<Member> member = memberRepository.findById(member_id);
        Member realMember = member.get();

        List<Post> results = realMember.getPosts();
        // results를 최신 순으로 정렬
        Collections.sort(results, Comparator.comparing(Post::getPost_create_time).reversed());

        List<ResponseGetMemberPostDto> getPostDtos = new ArrayList<>();
        if (results.size() == 0) {
            throw new NotFoundException();
        } else {
            getPostDtos = createResponseGetMemberPostDto(results, member_id);
        }
        return getPostDtos;
    }

    public List<ResponseGetMemberPostDto> getLikeMemberPost(Long member_id) throws NotFoundException {
        Member member = checkMember(member_id); //존재한다면 객체 생성
        List<Post> results = member.getLikedPosts();
        // results를 최신 순으로 정렬
        Collections.sort(results, Comparator.comparing(Post::getPost_create_time).reversed());
        List<ResponseGetMemberPostDto> getPostDtos = createResponseGetMemberPostDto(results, member_id);
        return getPostDtos;
    }
}