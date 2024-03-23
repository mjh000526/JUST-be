package com.example.just.Service;

import com.example.just.Dao.Notification;
import com.example.just.Dto.Message;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.NotificationRepository;
import com.example.just.Response.ResponseMessage;
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
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    public ResponseEntity getNotificationList(HttpServletRequest request,int page){
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        List<Notification> notifications = notificationRepository.findAllByReceiver(memberRepository.findById(member_id).get());
        List<ResponseNotification> result = notifications.stream()
                .map(noti -> new ResponseNotification(noti))
                .collect(Collectors.toList());

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

    public ResponseEntity checkNotification(HttpServletRequest request,Long noti_id){
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token));
        Notification notification = notificationRepository.findById(noti_id).get();
        if(notification.getReceiver().getId() != member_id){
            return new ResponseEntity(new ResponseMessage("사용자의 알림이 아닙니다."),HttpStatus.BAD_REQUEST);
        }
        notification.setNotIsRead(true);
        notificationRepository.save(notification);
        return new ResponseEntity(new ResponseNotification(notification), HttpStatus.OK);
    }
}
