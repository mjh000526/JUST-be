package com.example.just.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
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
                .createScoped(List.of("https://www.googleapi.com/auth/cloud-platform"));
        System.out.println(googleCredentials.getQuotaProjectId());
        System.out.println(googleCredentials);
        System.out.println(googleCredentials.getAuthenticationType());
        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }

    //fcm형태로 가공
//    public String makeMessage(final String targetToken, final Notification notification) throws JsonProcessingException {
//        final Long senderId = notification.ge
//    }

    public void sendMessageByToken(String title,String body, String token) throws FirebaseMessagingException{
        FirebaseMessaging.getInstance().send(Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(token)
                .build());
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
