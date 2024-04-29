package com.example.just.Config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

    //파이어베이스 암호키 파일
    @Value("${fcm.key-file}")
    private String serviceAccountFilePath;

    //파이어베이스 프로젝트 아이디
    @Value("${fcm.project-id}")
    private String projectId;

    //프로젝트가 실행될 때 최초 한번 파이어베이스와 연결
    @PostConstruct
    public void init(){
        try {
            FileInputStream serviceAccount =
                    new FileInputStream(serviceAccountFilePath);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
