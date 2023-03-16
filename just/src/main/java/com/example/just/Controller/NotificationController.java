package com.example.just.Controller;

import com.example.just.Service.NotificationService;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @ApiOperation(value = "알림", notes = "알림 발생")
    @GetMapping(value = "/noti", produces = "text/event-stream")
    @ResponseStatus(HttpStatus.OK)
    public SseEmitter subscribe(HttpServletRequest request, @RequestHeader(value = "Last_Event-ID",required = false,defaultValue = "") String lastEventId){
        return notificationService.subscribe(request,lastEventId);

    }
}
