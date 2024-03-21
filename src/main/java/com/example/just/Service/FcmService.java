package com.example.just.Service;

import com.example.just.Dto.FCMMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FcmService {
    private final ObjectMapper objectMapper;
    @Value("${fcm.url}")
    private  String FIREBASE_URL;

    @Autowired
    public FcmService(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    //fcm토큰받기
    private String getAccessToken() throws IOException{
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource("key/just-firebase-key.json").getInputStream())
                .createScoped(Arrays.asList("https://www.googleapi.com/auth/cloud-platform"));
        System.out.println(googleCredentials.getQuotaProjectId());
        System.out.println(googleCredentials);
        System.out.println(googleCredentials.getAuthenticationType());
        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }

    //fcm형태로 가공
    public String makeMessage(String targetToken, String title, String body,
                              String name, String description) throws JsonProcessingException {
        FCMMessageDto fcmMessage = FCMMessageDto.builder()
                .message(
                        FCMMessageDto.FCMMessage.builder()
                                .token(targetToken)
                                .notification(
                                        FCMMessageDto.Notification.builder()
                                                .title(title)
                                                .body(body)
                                                .build()
                                )
                                .data(
                                        FCMMessageDto.Data.builder()
                                                .name(name)
                                                .description(description)
                                                .build()
                                )
                                .build()
                )
                .validateOnly(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    public ResponseEntity sendMessageTo(String targetToken, String title, String body, String id, String isEnd)
            throws IOException {
        String message = makeMessage(targetToken,title,body,id,isEnd);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(FIREBASE_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION,"Bearer "+getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE,"application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
        return new ResponseEntity(response, HttpStatus.OK);
    }

    public ResponseEntity testMessage(String token) throws FirebaseMessagingException {
        String result = FirebaseMessaging.getInstance().send(Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("title")
                        .setBody("body")
                        .build())
                .setToken(token)
                .build());
        return new ResponseEntity(result,HttpStatus.OK);
    }
}
