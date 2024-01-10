package com.example.just.Controller;

import com.example.just.Service.NotificationService;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @ApiOperation(value = "알림", notes = "<big>알림 통신 연결</big>lastEventID는 값을 넣지않고, 헤더에 토큰만 있으면 됨\n" +
            "not_type : comment(댓글달림),postLike(게시글에 좋아요), commentLike(댓글좋아요), bigComment(대댓글달림)로 어떤 종류인지 판단, <big>String타입</big>\n" +
            "not_post_id : 알림의 target 게시글 id, <big>int</big> \n" +
            "not_datetime : 알람이온 시간(일단 필요해서 넣었음), <big>DateTime</big>\n" +
            "receiver : 수신자(게시글 작성자인 본인을 말함)id, <big>int</big>\n" +
            "senderId : 송신자id, <big>int</big>")
    @GetMapping(value = "/noti", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(HttpServletRequest request, @RequestHeader(value = "Last_Event-ID",required = false,defaultValue = "") String lastEventId){

        return notificationService.subscribe(request,lastEventId);

    }
}
