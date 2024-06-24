package com.example.just.Service;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Notification;
import com.example.just.Dao.Post;
import com.example.just.Document.PostDocument;
import com.example.just.Dto.*;
import com.example.just.Repository.CommentRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.NotificationRepository;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;
import com.example.just.Response.ResponseCommentDtoBefore;
import com.example.just.Response.ResponseMyCommentDto;
import com.example.just.Response.ResponsePostCommentDtoBefore;
import com.google.firebase.messaging.FirebaseMessagingException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.example.just.jwt.JwtProvider;
import com.example.just.Response.ResponsePostCommentDto;
import com.example.just.Response.ResponseCommentDto;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FcmService fcmService;

    @Autowired
    private NotificationRepository  notificationRepository;

    @Value("${fcm.token}")
    String fcmToken;

//    @Autowired
//    private NotificationService notificationService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    PostContentESRespository postContentESRespository;

    public Comment createComment(Long postId, Long member_id, CommentDto commentDto)
            throws FirebaseMessagingException, IllegalAccessException {
        // 부모 댓글이 있는 경우, 해당 부모 댓글을 가져옴
        Comment parentComment = null;
        //부모 댓글이 존재 하지 않을 경우
        if (commentDto.getParent_comment_id() != null && commentDto.getParent_comment_id() != 0) {
            parentComment = commentRepository.findById(commentDto.getParent_comment_id())
                    .orElseThrow(() -> new NullPointerException("부모 댓글이 존재하지 않습니다."));
            //부모로 하려는 댓글이 대댓글일 경우
            if (parentComment.getParent() != null) {
                throw new IllegalAccessException("해당 댓글에는 대댓글을 작성할 수 없습니다.");
            }
        }

        // 게시물이 있는지 확인하고 가져옴
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));
        Member member = memberRepository.findById(member_id).orElseGet(() -> new Member());
        //댓글 객체 생성
        Comment comment = new Comment();
        //비식별화 진행
        List<String> content = getConvertString(commentDto.getComment_content().strip());
        comment.setComment_content(content.get(0));
        comment.setPost(post);
        comment.setMember(member);
        comment.setParent(parentComment);
        comment.setComment_like(0L);
        comment.setComment_dislike(0L);
        LocalDateTime currentDateTime = LocalDateTime.now();
        Date currentTime = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
        comment.setComment_create_time(currentTime);
        comment.setBlamedCount(0);
        //해당 댓글을 작성한 게시글의 작성자 조회
        Optional<Member> receiver = memberRepository.findById(
                postRepository.findById(postId).get().getMember().getId());
        // 부모 댓글이 있을 경우, 자식 댓글로 추가
        if (parentComment != null) {
            parentComment.getChildren().add(comment);
            //댓글과 부모댓글의 작성자가 다를 경우 알림 이력 저장
            if(parentComment.getMember().getId() != member_id){
                notificationRepository.save(Notification.builder()
                        .notObjectId(parentComment.getComment_id())
                        .notType("comment")
                        .notIsRead(false)
                        .receiver(parentComment.getMember())
                        .senderId(member_id)
                        .build()
                );
                //부모댓글의 작성자에게 알림 전송
//                fcmService.sendMessageByToken("댓글 알림","누군가가 댓글에 대댓글을 작성했어요! \""
//                        + commentDto.getComment_content() + "\"",parentComment.getMember().getFcmToken());
            }
        } else if (parentComment == null) { //아닐경우는 부모댓글
            PostDocument postDocument = postContentESRespository.findById(postId).get();
            postDocument.setCommentSize(postDocument.getCommentSize() + 1);
            postContentESRespository.save(postDocument);
            //게시글의 작성자 조회
            Member noti_member = memberRepository.findById(postDocument.getMemberId()).get();
            //게시글과 댓글 작성자가 일치 하지 않을 경우
            if(noti_member.getId() != member_id){
                //알림 이력 저장
                notificationRepository.save(Notification.builder()
                        .notObjectId(postDocument.getId())
                        .notType("post")
                        .notIsRead(false)
                        .receiver(noti_member)
                        .senderId(member_id)
                        .build()
                );
                //게시글의 작성자에게 알림 발송
//                fcmService.sendMessageByToken("댓글 알림","누군가가 게시글에 댓글을 작성했어요! \""
//                        + commentDto.getComment_content() + "\"",receiver.get().getFcmToken());
            }
        }
        //댓글 테이블에 저장
        return commentRepository.save(comment);
    }

    //postId에 해당하는 댓글 조회 v2
    public ResponsePostCommentDto getCommentList(Long postId, HttpServletRequest req) {
        //해당 postId 게시글 조회 및 없을 경우 예외처리
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));
        Long member_id;
        //헤더에서 토큰 추출 및 id확인
        if (req.getHeader("Authorization") != null) {
            String token = jwtProvider.getAccessToken(req);
            member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        } else {
            member_id = 0L;
        }

        //DB의 댓글 데이터를 ResponseCommentDto 포맷으로 변환
        List<ResponseCommentDto> comments = new ArrayList<>();
        if (post != null && post.getComments() != null) {
            comments = post.getComments().stream()
                    .filter(comment -> comment.getParent() == null)
                    .map(comment -> new ResponseCommentDto(comment, member_id, ""))
                    .collect(Collectors.toList());
        }

        return new ResponsePostCommentDto(post.getPostContent(), comments);
    }

    //postId에 해당하는 댓글 조회 v1
    public ResponsePostCommentDtoBefore getCommentListBefore(Long postId, HttpServletRequest req) {
        //해당 postId 게시글 조회 및 없을 경우 예외처리
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));
        Long member_id;

        //헤더에서 토큰 추출 및 id확인
        if (req.getHeader("Authorization") != null) {
            String token = jwtProvider.getAccessToken(req);
            member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        } else {
            member_id = 0L;
        }

        //DB의 댓글 데이터를 ResponseCommentDto 포맷으로 변환
        List<ResponseCommentDtoBefore> comments = post.getComments().stream()
                .map(comment -> new ResponseCommentDtoBefore(comment, member_id))
                .collect(Collectors.toList());
        return new ResponsePostCommentDtoBefore(post.getPostContent(), comments);
    }

    //댓글 삭제
    public ResponseEntity<String> deleteComment(Long postId, Long commentId, Long member_id) {

        //해당 postId 게시글 조회 및 없을 경우 예외처리
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));

        //해당 댓글id 조회 및 없을 경우 예외처리
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("해당 댓글이 존재하지 않습니다."));

        //클라이언트와 댓글 작성자가 일치한지 확인
        if (comment.getMember().getId() == member_id) {
            comment.setChildren(null); //자식 댓글들 삭제처리
            //ELK의 게시글 객체 조회
            PostDocument postDocument = postContentESRespository.findById(postId).get();
            //게시글 객체의 댓글 카운트 감소
            postDocument.setCommentSize(postDocument.getCommentSize() - 1);
            postContentESRespository.save(postDocument);
            //DB에서 댓글 데이터 삭제
            commentRepository.deleteById(commentId);
            //해당 댓글에 관련된 알림이력 삭제
            notificationRepository.deleteAllByNotObjectIdAndNotType(commentId,"comment");
            return ResponseEntity.ok("ok");
        } else {
            //일치하지 않으면 삭제 불가
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }
    }

    //댓글 수정
    public ResponseEntity<String> putComment(Long postId, Long commentId,
                                             PutCommentDto commentDto, Long member_id) {
        //수정한 댓글 id 조회
        Comment comment = commentRepository.findById(commentId).get();
        //수정할 댓글 정보가 존재하지 않을 경우
        if (comment == null) {
            ResponseEntity.status(404).body("댓글이 존재하지 않습니다.");
        }

        //수정할 댓글을 가지는 게시글 정보 조회
        Post post = postRepository.findById(postId).get();
        //해당 게시글이 없을 경우
        if (post == null) {
            ResponseEntity.status(404).body("게시물이 존재하지 않습니다.");
        }

        //클라이언트와 수정할 댓글의 작성자가 일치할 경우
        if (comment.getMember().getId() == member_id) {
            comment.setComment_content(commentDto.getComment_content());
            // 업데이트된 댓글을 저장합니다.
            commentRepository.save(comment);

            return ResponseEntity.ok(comment.getComment_content());
        } else {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }
    }

    //댓글 신고
    public ResponseEntity<String> blameComment(Long postId, Long commentId) {

        //댓글id 조회 및 없을 경우 예외발생
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 존재하지 않습니다: " + commentId));

        //게시글id 조회 및 없을 경우 예외발생
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));

        //댓글의 신고받은 횟수 증가 후 저장
        comment.setBlamedCount(comment.getBlamedCount() + 1);
        commentRepository.save(comment);

        return ResponseEntity.ok("ok");
    }

    public int blameGetComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 존재하지 않습니다: " + commentId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));
        return comment.getBlamedCount();
    }

    //댓글 좋아요
    @Transactional
    public void likeComment(Long postId, Long commentId, Long member_id) {
        //해당 댓글id 조회 및 없을 경우 예외발생
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 존재하지 않습니다: " + commentId));
        //해당 회원id 조회 및 없을 경우 빈 객체생성
        Member member = memberRepository.findById(member_id).orElseGet(() -> new Member());
        //해당 게시글id 조회 및 없을 경우 예외발생
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));
        //해당 댓글에 사용자가 좋아요 목록에 포함되어있으면 좋아요 취소 아니면 좋아요 추가
        if (comment.getLikedMembers().contains(member)) {
            comment.removeLike(member);
        } else {
            comment.addLike(member);
        }
        //notificationService.send(post.getMember(), "commentLike", post.getPost_id(), member_id);
        commentRepository.save(comment);
    }

    //자신이 작성한 댓글 조회
    public ResponseEntity getMyComment(Long member_id) {
        //id로 회원객체 조회
        Member member = memberRepository.findById(member_id).get();

        //해당 회원이 작성한 댓글 전체 리스트 조회
        List<Comment> comments = commentRepository.findAllByMember(member);
        //최신순으로 정렬
        Collections.sort(comments, Comparator.comparing(Comment::getComment_create_time).reversed());
        //ResponseMyCommentDto의 데이터 포맷에 맞게 변환
        List<ResponseMyCommentDto> result = comments.stream()
                .map(comment -> new ResponseMyCommentDto(comment, member_id, member, true))
                .collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //모든 댓글 조회
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<String> getConvertString(String str) { // 문자열 변환
        RestTemplate restTemplate = new RestTemplate();

        String requestBody = "{\"content\": [";

        requestBody += "\"" + str+"\", ";
        requestBody += "]}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject parameter = new JSONObject();
        parameter.put("content",str);
        HttpEntity<String> request = new HttpEntity<>(parameter.toJSONString(), headers);
        System.out.println(parameter.toJSONString());
        ResponseEntity<String> responseEntity = restTemplate.exchange(
//                "http://"+server_address+":8081/api/ner/post",
                "http://localhost:8081/api/ner/post",
                HttpMethod.POST,
                request,
                String.class);
        String responseBody = responseEntity.getBody();
        return parsingJson(responseBody);
    }

    public List<String> parsingJson(String json) { // JSON 파싱
        JSONArray jsonArray;
        List<String> denyList = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject elem = (JSONObject) parser.parse(json);
            jsonArray = (JSONArray) elem.get("deny_list");
            for (Object obj : jsonArray) {
                denyList.add((String) obj);
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return denyList;
    }
}
