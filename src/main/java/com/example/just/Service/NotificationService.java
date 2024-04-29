package com.example.just.Service;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Notification;
import com.example.just.Dao.Post;
import com.example.just.Dto.Message;
import com.example.just.Repository.CommentRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.NotificationRepository;
import com.example.just.Repository.PostRepository;
import com.example.just.Response.ResponseMessage;
import com.example.just.Response.ResponseNotiCommentDto;
import com.example.just.Response.ResponseNotiPostDto;
import com.example.just.Response.ResponseNotification;

import com.example.just.jwt.JwtProvider;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {//알림 관련 서비스
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    //알림 목록 조회
    public ResponseEntity getNotificationList(HttpServletRequest request,int page){

        //헤더의 토큰으로 클라이언트 id 추출
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token));

        //해당 id로 수신된 알림 리스트 조회
        List<Notification> notifications = notificationRepository.findAllByReceiver(memberRepository.findById(member_id).get());

        //ResponseNotification 포맷에 맞게 변환
        List<ResponseNotification> result = notifications.stream()
                .map(noti -> {
                    //게시글에 대한 알림일 경우 ResponseNotiPostDto 포맷으로 변환
                    if(noti.getNotType().equals("post")){
                        Post post = postRepository.findById(noti.getNotObjectId()).get();
                        return new ResponseNotification(noti,new ResponseNotiPostDto(post));
                    }
                    else {
                        //댓글에 대한 알림일 경우 ResponseNotiCommentDto 포맷으로 변환
                        Comment comment = commentRepository.findById(noti.getNotObjectId()).get();
                        return new ResponseNotification(noti,new ResponseNotiCommentDto(comment));
                    }
                })
                .collect(Collectors.toList());

        //알림 리스트 페이지네이션
        PageRequest pageRequest = PageRequest.of(page,10);
        result.sort(Comparator.comparing(ResponseNotification::getNot_datetime).reversed());//최신순 조회
        int start = (int) pageRequest.getOffset();
        if (start >= result.size()) {
            return new ResponseEntity(new ResponseMessage("페이지를 초과하엿습니다."),null, HttpStatus.BAD_REQUEST);
        }
        int end = Math.min((start + pageRequest.getPageSize()),result.size());
        Page<ResponseNotification> postPage = new PageImpl<>(result.subList(start,end), pageRequest, result.size());
        return ResponseEntity.ok(postPage);
    }

    //알림 읽음 여부
    public ResponseEntity checkNotification(HttpServletRequest request,Long noti_id){
        //헤더의 토큰으로부터 id값추출
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token));
        //noti_id를 가진 데이터 조회
        Notification notification = notificationRepository.findById(noti_id).get();
        //조회한 알림의 회원가 요청한 회원이 불일치할 경우
        if(notification.getReceiver().getId() != member_id){
            return new ResponseEntity(new ResponseMessage("사용자의 알림이 아닙니다."),HttpStatus.BAD_REQUEST);
        }
        //조회한 데이터의 읾음여부값 변경
        notification.setNotIsRead(true);
        notificationRepository.save(notification);
        //게시글에 대한 알림일 경우 ResponseNotiPostDto 포맷으로 변환
        if(notification.getNotType().equals("post")){
            Post post = postRepository.findById(notification.getNotObjectId()).get();
            return new ResponseEntity(new ResponseNotification(notification,new ResponseNotiPostDto(post)),HttpStatus.OK);
        }
        else{
            //댓글에 대한 알림일 경우 ResponseNotiPostDto 포맷으로 변환
            Comment comment = commentRepository.findById(notification.getNotObjectId()).get();
            return new ResponseEntity(new ResponseNotification(notification,new ResponseNotiCommentDto(comment)),HttpStatus.OK);
        }

    }

    //모두 읽음 처리
    public ResponseEntity allCheckNotification(HttpServletRequest request){
        //헤더의 토큰으로부터 id값추출
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token));
        //해당 회원 아이디로 수신된 알림 전체 조회
        List<Notification> notifications = notificationRepository.findAllByReceiver(memberRepository.findById(member_id).get());
        //조회된 알림들의 읽음여부를 모두 true로 변경
        for (Notification notification : notifications) {
            notification.setNotIsRead(true);
        }
        notificationRepository.saveAll(notifications);
        return new ResponseEntity(new ResponseMessage("읽음 처리 완료"),HttpStatus.OK);
    }
}
