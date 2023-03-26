package com.example.just;


import java.net.URL;
import java.net.HttpURLConnection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JustApplicationTest {


    private int port=8080;

    @Test
    public void testServerIsRunning() throws Exception {
        String url = "http://localhost:" + port + "/";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int statusCode = connection.getResponseCode();
        assertThat(statusCode).isEqualTo(200);
    }
}