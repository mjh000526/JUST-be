package com.example.just.Service;


import com.example.just.Dao.HashTag;
import com.example.just.Dao.HashTagMap;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;


import com.example.just.Dao.PostContent;
import com.example.just.Dao.QBlame;
import com.example.just.Dao.QPost;
import com.example.just.Document.HashTagDocument;
import com.example.just.Document.PostDocument;
import com.example.just.Dto.DenyListDto;
import com.example.just.Dto.GptRequestDto;
import com.example.just.Dto.PostContentDto;
import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.example.just.Repository.BlameRepository;

import com.example.just.Repository.HashTagESRepository;
import com.example.just.Repository.HashTagMapRepository;
import com.example.just.Response.ResponseGetMemberPostDto;
import com.example.just.Response.ResponseGetPostDto;
import com.example.just.Response.ResponsePutPostDto;
import com.example.just.Mapper.PostMapper;
import com.example.just.Repository.HashTagRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;

import com.example.just.jwt.JwtProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;


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
    private HashTagESRepository hashTagESRepository;
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
    @Autowired
    private HashTagMapRepository hashTagMapRepository;

    @Value("${server-add}")
    private String serverAddress;

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
        //해시태그가 NULL일 경우 Gpt로 해시태그 생성
        if (postDto.getHash_tag() == null) {
            String prompt = "";
            for (int i = 0; i < postDto.getPost_content().size(); i++) {
                prompt += postDto.getPost_content().get(i) + " ";
            }
            GptRequestDto gptRequestDto = new GptRequestDto(prompt);
            List<String> tag = gptService.getTag(gptRequestDto);
            postDto.setHash_tag(tag);
        }
        List<PostContent> postContents = new ArrayList<>();
        for (String content : postDto.getPost_content()) {
            PostContent postContent = new PostContent();
            postContent.setContent(content);
            postContent.setPost(post);
            postContents.add(postContent);
        }

        post.writePost(postDto, postContents, member);
        Post p = postRepository.save(post);

        List<String> hashTags = postDto.getHash_tag();
        saveHashTag(hashTags, p);
        System.out.println(p);
        postContentESRespository.save(new PostDocument(p));
        return postDto;
    }

    private void saveHashTag(List<String> hashTags, Post p) {
        for (int i = 0; i < hashTags.size(); i++) {
            HashTag hashTag = hashTagRepository.findByName(hashTags.get(i));
            HashTagMap hashTagMap = new HashTagMap();
            if (hashTag == null) {
                HashTag newHashTag = new HashTag(hashTags.get(i));
                newHashTag.setTagCount(1L);
                newHashTag = hashTagRepository.save(newHashTag);
                hashTagESRepository.save(new HashTagDocument(newHashTag));
                hashTagMap = new HashTagMap(newHashTag, p); //객체 그래프 설정
            } else {
                hashTag.setTagCount(hashTag.getTagCount() + 1);
                hashTagRepository.save(hashTag);
                hashTagESRepository.save(new HashTagDocument(hashTag));
                hashTagMap = new HashTagMap(hashTag, p); //객체 그래프 설정
            }
            hashTagMapRepository.save(hashTagMap);
        }
    }


    //글 삭제
    public void deletePost(Long post_id) throws NotFoundException {
        Post post = checkPost(post_id);
        if (post == null) {
            throw new NotFoundException();
        } else {
            // Elasticsearch에서 해당 포스트의 내용 삭제
            postContentESRespository.deleteById(post_id);
            deleteHashTag(post);
            postRepository.deleteById(post_id);
        }
    }

    //글 수정
    public ResponsePutPostDto putPost(Long member_id, PutPostDto postDto) throws NotFoundException {
        Long post_id = postDto.getPost_id();
        Member member = checkMember(member_id);
        Post checkPost = checkPost(post_id);
        List<HashTagMap> hashTagMaps = checkPost.getHashTagMaps();


        deleteHashTag(checkPost);


        List<PostContent> postContents = new ArrayList<>();
        for (String content : postDto.getPost_content()) {
            PostContent postContent = new PostContent();
            postContent.setContent(content);
            postContent.setPost(checkPost);
            postContents.add(postContent);
        }

        checkPost.changePost(postDto, member, checkPost, postContents);


        Post p = postRepository.save(checkPost);
        saveHashTag(postDto.getHash_tage(), p);

        postContentESRespository.save(new PostDocument(checkPost));

        ResponsePutPostDto responsePutPostDto = new ResponsePutPostDto(p);
        return responsePutPostDto;
    }

    private void deleteHashTag(Post post) {
        List<HashTagMap> hashTagMaps = post.getHashTagMaps();
        for (int i = 0; i < hashTagMaps.size(); i++) {
            hashTagRepository.findById(hashTagMaps.get(i).getHashTag().getId())
                    .ifPresent(
                            hashTag -> {
                                if (hashTag.getTagCount() != 1) {
                                    hashTag.setTagCount(hashTag.getTagCount() - 1);
                                    hashTagESRepository.save(new HashTagDocument(hashTag));
                                    hashTagRepository.save(hashTag);
                                } else {
                                    hashTagESRepository.deleteById(hashTag.getId());
                                    hashTagRepository.deleteById(hashTag.getId());
                                }
                            });
        }
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
            List<HashTagMap> hashTagMaps = results.get(i).getHashTagMaps();
            ResponseGetMemberPostDto responseGetMemberPostDto = new ResponseGetMemberPostDto(results, member_id, i,
                    hashTagMaps);
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
            postDocument.setPost_like_size(postDocument.getPost_like_size() - 1);
            responsePost = new ResponsePost(post_id, "좋아요 취소");
        } else {
            post.addLike(member);
            postDocument.setPost_like_size(postDocument.getPost_like_size() + 1);
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

    public String getConvertString(String str){
        RestTemplate restTemplate = new RestTemplate();

        String requestBody = "{\"question\":\"" + str + "\",\"deny_list\":[\"string\"]}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(requestBody,headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://203.241.228.51:8000/anonymize/",
                HttpMethod.POST,
                request,
                String.class);

        String responseBody = responseEntity.getBody();
        String convertStr =parsingJson(responseBody);
        return convertStr;
    }

    public DenyListDto getPororo(PostContentDto content) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(content);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(requestBody,headers);

        ResponseEntity<DenyListDto> responseEntity = restTemplate.exchange(
                "http://" + serverAddress + ":8081/api/ner/post",
                HttpMethod.POST,
                request,
                DenyListDto.class);

        DenyListDto responseBody = responseEntity.getBody();
        return responseBody;
    }

    public String parsingJson(String json){
        String response;
        try {
            JSONParser parser = new JSONParser();
            JSONObject elem = (JSONObject) parser.parse(json);
            response = elem.get("convertedQuestion").toString();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public ResponseEntity insertDataset(Long memberId){
        String[] name = {"준호는 ","윌슨은 ","나는 ","그는 "};
        String[] things = {"라면 사리를 ","자바를 ","재미있는 게임을 ","김해에 있는 학교를 ",
                "서울에 있는 친적집을 ", "해외에 있는 별장을 ","제일 친한 친구 집을 ","유명한 맛집을 "};
        String[] active = {"방문했다.", "놀았다", "먹었다","살고 있다","등교했다.",
                "요리했다.","사용했다.","공부했다","플레이했다.","연구했다."};
        Member member = memberRepository.findById(memberId).get();
        for(int i=0;i<3000;i++){
            for(int j=0;j< things.length;j++){
                for(int x=0;x<name.length;x++){
                    for(int y=0;y<active.length;y++){
                        Post post = new Post();
                        List<PostContent> postContents = new ArrayList<>();
                        PostContent postContent = new PostContent();
                        postContent.setContent(name[x]+things[j]+active[y]);
                        postContent.setPost(post);
                        postContents.add(postContent);
                        post.setPostContent(postContents);
                        post.setMember(member);
                        postRepository.save(post);
                    }
                }
            }
        }
        return new ResponseEntity("ok",HttpStatus.OK);
    }

}