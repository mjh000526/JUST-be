package com.example.just;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class JustApplicationTest {
    @Test
    public void testServerIsRunning() throws Exception {
        String url = "http://43.201.174.163:8080";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int statusCode = connection.getResponseCode();
        assertThat(statusCode).isEqualTo(200);
    }
}