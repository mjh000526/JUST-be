package com.example.just.Service;


import com.example.just.Dao.HashTag;
import com.example.just.Dao.HashTagMap;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;


import com.example.just.Dao.QBlame;
import com.example.just.Dao.QHashTag;
import com.example.just.Dao.QHashTagMap;
import com.example.just.Dao.QPost;
import com.example.just.Document.HashTagDocument;
import com.example.just.Document.PostDocument;
import com.example.just.Dto.GptRequestDto;
import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.example.just.Repository.BlameRepository;

import com.example.just.Repository.HashTagESRepository;
import com.example.just.Repository.HashTagMapRepository;
import com.example.just.Response.ResponseGetMemberPostDto;
import com.example.just.Response.ResponsePutPostDto;
import com.example.just.Mapper.PostMapper;
import com.example.just.Repository.HashTagRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;

import com.example.just.jwt.JwtProvider;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
        List<String> content = new ArrayList<>();
        for (int i = 0; i < postDto.getPost_content().size(); i++) {
            content.add(getConvertString(postDto.getPost_content().get(i)));
        }
        postDto.setPost_content(content);
        post.writePost(postDto, member);
        Post p = postRepository.save(post);

        List<String> hashTags = postDto.getHash_tag();
        saveHashTag(hashTags, p);

        PostDocument postDocument = new PostDocument(p);
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

        List<String> content = new ArrayList<>();
        for (int i = 0; i < postDto.getPost_content().size(); i++) {
            content.add(getConvertString(postDto.getPost_content().get(i)));
        }
        postDto.setPost_content(content);

        checkPost.changePost(postDto, member, checkPost);

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
            postDocument.setPostLikeSize(postDocument.getPostLikeSize() - 1);
            responsePost = new ResponsePost(post_id, "좋아요 취소");
        } else {
            post.addLike(member);
            postDocument.setPostLikeSize(postDocument.getPostLikeSize() + 1);
            responsePost = new ResponsePost(post_id, "좋아요 완료");
        }
        postContentESRespository.save(postDocument);
        Post savePost = postRepository.save(post);

        return ResponseEntity.ok(responsePost);
    }

    public ResponseGetPost searchByCursorMember(String cursor, Long limit, Long member_id, String like)
            throws NotFoundException, IOException {
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
        HttpClient httpClient = HttpClients.createDefault();
        List<String> likePostHashTagName = new ArrayList<>();
        System.out.println(like);
        // 이전에 본 글들의 ID를 가져옵니다.
        // Base64로 디코딩
        byte[] decodedBytes = Base64.getDecoder().decode(like);

// UTF-8으로 디코딩된 문자열
        like = new String(decodedBytes, StandardCharsets.UTF_8);
        System.out.println(like);
        if (like != null) {
            String[] likePostArray = like.split(", ");
            for (String likePost : likePostArray) {
                likePostHashTagName.add((likePost.trim()));
            }
        }
        Random random = new Random();
        int arrayLength = likePostHashTagName.size();
        int randomIndex = random.nextInt(arrayLength);
        String randonHashTagName = likePostHashTagName.get(randomIndex);
        // 요청을 보낼 URL 설정
        HttpGet request = new HttpGet("http://34.22.67.43:8081/api/similar_words/" + randonHashTagName);

        // 요청 실행 및 응답 수신
        HttpResponse response = httpClient.execute(request);

        // 응답 코드 확인
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("Response Code: " + statusCode);

        // 응답 데이터 읽기
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println("Response: " + responseBody);//Response: [[3], [1], [1], [1]]
        // 여기서 Python Server의 추천 시스템으로 Post_id들을 가져온다.
        List<Long> postIds = new ArrayList<>();
        for (int i = 2; i < responseBody.length(); i += 5) {
            postIds.add(Long.parseLong(responseBody.substring(i, i + 1)));
        }
        System.out.println(postIds);
        // 중복된 글을 제외하고 랜덤으로 limit+1개의 글을 가져옵니다.
        List<Post> results = query.select(post)
                .from(post)
                .where(post.post_id.notIn(viewedPostIds),
                        post.post_create_time.isNotNull(),
                        post.post_id.notIn(blames),
                        post.member.id.notIn(targetMembers),
                        post.post_id.in(postIds))
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

    public String getConvertString(String str) {
        RestTemplate restTemplate = new RestTemplate();

        String requestBody = "{\"question\":\"" + str + "\",\"deny_list\":[\"string\"]}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://203.241.228.51:8000/anonymize/",
                HttpMethod.POST,
                request,
                String.class);

        String responseBody = responseEntity.getBody();
        String convertStr = parsingJson(responseBody);
        return convertStr;
    }

    public String parsingJson(String json) {
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

    public String getLikeHashTag(Long member_id) {

        QHashTagMap hashTagMap = QHashTagMap.hashTagMap;
        QHashTag hashTag = QHashTag.hashTag;
        Member member = checkMember(member_id);
        //회웡니 쓴글 다 가져오기
        List<Post> posts = member.getPosts();

        //회원이 좋아요 한글의 해시태그 ID 가져오기
        List<Long> hashTagMapsOfLike = query.select(hashTagMap.id)
                .from(hashTagMap)
                .where(hashTagMap.post.in(member.getLikedPosts()))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(5)
                .fetch();

        //회원이 쓴글의 해시태그 ID 가져오기
        List<Long> hashTagMaps = query.select(hashTagMap.id)
                .from(hashTagMap)
                .where(hashTagMap.post.in(posts))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(5)
                .fetch();

        for (int i = 0; i < hashTagMapsOfLike.size(); i++) {
            hashTagMaps.add(hashTagMapsOfLike.get(i));
        }

        //해시태그맵의 ID랑 겹치는거 뽑아오기
        List<String> hashTags = query.select(hashTag.name)
                .from(hashTag)
                .where(hashTag.id.in(hashTagMaps))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(5)
                .fetch();
        String hashTagsString = hashTags.toString();
        // hashTags.toString()으로부터 UTF-8로 인코딩된 바이트 배열 생성
        byte[] utf8Bytes = hashTagsString.getBytes(StandardCharsets.UTF_8);

// UTF-8로 인코딩된 바이트 배열을 Base64로 인코딩하여 문자열로 변환
        String encodeHashTag = Base64.getEncoder().encodeToString(utf8Bytes);
        System.out.println(hashTags);

        return encodeHashTag;
    }
}