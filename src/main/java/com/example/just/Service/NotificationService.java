package com.example.just.Service;

import com.example.just.Dao.Member;
import com.example.just.Dao.Notification;
import com.example.just.Dto.NotificationDto;
import com.example.just.Repository.EmitterRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.NotificationRepository;
import com.example.just.Repository.PostRepository;
import com.example.just.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

import java.util.Map;

@Service
public class NotificationService {

    private static  final Long timeout=60L*1000*60;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EmitterRepository emitterRepository;

    @Autowired
    private TokenProvider tokenProvider;

    public SseEmitter subscribe(HttpServletRequest request, String lastEventId){
        String token = (String) request.getHeader("access_token");
        Long id = Long.valueOf(tokenProvider.getIdFromToken(token)); //토큰으로 id추출;
        String emitterId = makeTimeIncludeId(id);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(timeout));
        emitter.onCompletion(()-> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(()-> emitterRepository.deleteById(emitterId));

        //더미이벤트를 전송하지 않으면 오류 발생 가능
        String eventId = makeTimeIncludeId(id);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId="+id+"]");

        //클라이언트가 미수신한 Event목록이 존재할 경우 전송하여 Event유실 발생
        if(hasLostData(lastEventId)){
            sendLostData(lastEventId,id,emitterId,emitter);
        }
        return emitter;
    }

    private String makeTimeIncludeId(Long id){
        return id + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data){
        try{
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        }catch (IOException e){
            emitterRepository.deleteById(emitterId);
        }
    }
    //받지 못한 데이터가 있는지 확인
    private boolean hasLostData(String lastEventId){
        return !lastEventId.isEmpty();
    }
    //받지못한 데이터가 있다면 Last 이벤트를 기준으로 그 뒤의 데이터를 추출해서 전송
    private void sendLostData(String lastEventId, Long id, String emitterId, SseEmitter emitter){
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithById(String.valueOf(id));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey())<0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    public void send(Member receiver, String notificationType, String content){
        Notification notification = notificationRepository.save(createNotification(receiver,notificationType,content));
        String receiverId = String.valueOf(receiver.getId());
        String eventId = receiverId + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithById(receiverId);
        emitters.forEach(
                (key, emitter) ->{
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, NotificationDto.create(notification));
                }
        );
    }

    //알림객체생성
    private Notification createNotification(Member receiver, String notificationType, String content){
        return Notification.builder()
                .not_type(notificationType)
                .not_content(content)
                .not_isRead(false)
                .not_datetime(new Date(System.currentTimeMillis()))
                .not_read_dateTime(null)
                .member(receiver)
                .build();
    }
}