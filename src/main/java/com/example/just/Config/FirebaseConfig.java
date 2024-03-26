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

    @Value("${fcm.key-file}")
    private String serviceAccountFilePath;

    @Value("${fcm.project-id}")
    private String projectId;
    @PostConstruct
    public void init(){
        System.out.println("컨피그는실행됨");
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("key/just-firebase-key.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            System.out.println("firebase 실행 : " + options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
