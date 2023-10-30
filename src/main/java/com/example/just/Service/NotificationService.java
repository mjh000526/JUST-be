package com.example.just.Service;

import com.example.just.Dao.Member;
import com.example.just.Dao.Notification;
import com.example.just.Dto.NotificationDto;
import com.example.just.Repository.EmitterRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.NotificationRepository;
import com.example.just.Repository.PostRepository;
import com.example.just.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

import java.util.Map;

@Service
public class NotificationService {

    private static final Long timeout=60L*1000*6000;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EmitterRepository emitterRepository;

    @Autowired
    private JwtProvider jwtProvider;

    public SseEmitter subscribe(HttpServletRequest request, String lastEventId){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        String emitterId = makeTimeIncludeId(id);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(timeout));
        emitter.onCompletion(()-> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(()-> emitterRepository.deleteById(emitterId));

        sendNotification(emitter, emitterId, "EventStream Created. [userId="+id+"]");

        //클라이언트가 미수신한 Event목록이 존재할 경우 전송하여 Event유실 발생
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithById(String.valueOf(id));
            System.out.println(events.size());
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendNotification(emitter, entry.getKey(), entry.getValue()));
        }
        return emitter;
    }

    private String makeTimeIncludeId(Long id){
        return id + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String id, Object data){
        try{
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name(id)
                    .data(data));

        }catch (IOException e){
            emitterRepository.deleteById(id);
        }
    }

    public void send(Member receiver, String notificationType, Long postId, Long senderId){
        Notification notification = notificationRepository.save(createNotification(receiver,notificationType,postId,senderId));
        String receiverId = String.valueOf(receiver.getId());
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithById(receiverId);
        emitters.forEach(
                (key, emitter) ->{
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, key, NotificationDto.create(notification));
                }
        );
    }

    //알림객체생성
    private Notification createNotification(Member receiver, String notificationType, Long postId, Long senderId){
        return Notification.builder()
                .notType(notificationType)
                .notPostId(postId)
                .notIsRead(false)
                .notDatetime(new Date(System.currentTimeMillis()))
                .receiver(receiver)
                .senderId(senderId)
                .build();
    }
}