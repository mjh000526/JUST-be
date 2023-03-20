package com.example.just;


import com.example.just.Controller.NotificationController;
import com.example.just.Dao.Member;
import com.example.just.Repository.MemberRepository;
import com.example.just.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/*
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class NotificationControllerTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    NotificationController notificationController;

    @BeforeEach
    void beforeEach(){
        mockMvc = webAppContextSetup(context).apply(springSecurity()).build();
    }
    @Test
    public void sub() throws Exception{
        //given
        Member member = new Member(1L,null,null,null,null,null,null);
        HashMap<String, String> token= new HashMap<>();
        token.put("user_id",member.getId().toString());
        String access = jwtProvider.generateToken(token);

        //when,then
        mockMvc.perform(get("/noti").header("X-AUTH-TOKEN",access)).andExpect(status().isOk());
    }
}

 */
