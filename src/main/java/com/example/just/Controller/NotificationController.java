package com.example.just.Controller;

import com.example.just.Service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "알림 조회", description = "헤더에 있는 값으로 판별\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "{\n"
                    + "  \"not_id\": \"\",\n"
                    + "  \"not_type\": 0\n"
                    + "  \"not_datetime\": \"\",\n"
                    + "  \"not_is_read\": 0\n"
                    + "  \"receiver_id\": 0\n"
                    + "  \"sender_id\": true\n"
                    + "}"),
            @ApiResponse(responseCode = "404", description = "{\n"
                    + "  \"message\": \"페이지를 초과하였습니다.\"\n"
                    + "}")
    })
    @GetMapping("/notifications")
    public ResponseEntity getNotificationList(HttpServletRequest request,int page){
        return notificationService.getNotificationList(request,page-1);
    }

    @Operation(summary = "알림 읽음 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "{\n"
                    + "  \"not_id\": \"\",\n"
                    + "  \"not_type\": 0\n"
                    + "  \"not_datetime\": \"\",\n"
                    + "  \"not_is_read\": 0\n"
                    + "  \"receiver_id\": 0\n"
                    + "  \"sender_id\": true\n"
                    + "}"),
            @ApiResponse(responseCode = "404", description = "{\n"
                    + "  \"message\": \"사용자의 알림이 아닙니다.\"\n"
                    + "}")
    })
    @PutMapping("/check/notifications")
    public ResponseEntity checkNotification(HttpServletRequest request,@RequestParam Long not_id){
        return notificationService.checkNotification(request,not_id);
    }

    @Operation(summary = "알림 전체 읽음")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "{\n"
                    + "  \"message\": \"읽음 처리 완료\",\n"
                    + "}"),
    })
    @PutMapping("/check/all/notifications")
    public ResponseEntity checkAllNotification(HttpServletRequest request){
        return notificationService.allCheckNotification(request);
    }
}
