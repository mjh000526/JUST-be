package com.example.just;


import java.net.URL;
import java.net.HttpURLConnection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest
public class JustApplicationTest {


    private int port=8081;
    static{
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }
    @Test
    public void testServerIsRunning() throws Exception {


        System.out.println("!");
    }
}