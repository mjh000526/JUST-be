package com.example.just;


import com.example.just.Dao.Member;
import com.example.just.Dao.Role;
import com.example.just.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MemberTest {
    @Autowired
    private JwtProvider jwtProvider;

    @Value("${jwt.secret}")
    private String secret;

    private final long access_token_time = (1000 * 60) * 60 * 24 * 30L;//60분 * 24 * 30 = 30일

    private Member member = null;

    @BeforeEach
    public void createMember(){
        member = Member.builder().id(1L).authority(Role.ROLE_USER).
                blameCount(0).
                blamedCount(0).
                email("fij@fjisjf").
                provider("provider").
                provider_id("12341234").
                refreshToken(null)
                .build();
    }
}
