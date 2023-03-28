package com.example.just;


import java.net.URL;
import java.net.HttpURLConnection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JustApplicationTest {


    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    @Test
    public void testServerIsRunning() throws Exception {
        // HTTP 요청 등 서버가 제대로 실행되는지 확인하는 코드 작성
        System.out.println("!"); // 랜덤 포트 할당되는 경우를 대비한 코드
    }
}