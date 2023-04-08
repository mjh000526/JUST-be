package com.example.just;


import com.example.just.Dao.Member;
import com.example.just.Dao.Role;
import com.example.just.Repository.MemberRepository;
import com.example.just.jwt.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemberTest {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Value("${jwt.secret}")
    private String secret;

    private final long access_token_time = (1000 * 60) * 60 * 24 * 30L;//60분 * 24 * 30 = 30일

    private Member member = new Member();

    @BeforeEach
    private void createMember(){
        member = Member.builder()
                .id(1L)
                .authority(Role.ROLE_USER)
                .blameCount(0)
                .blamedCount(0)
                .email("sdf@asd")
                .nickname("테스트용")
                .provider("kakao")
                .provider_id("12341234")
                .refreshToken("").build();
    }
    @Test
    @DisplayName("토큰 생성")
    public void createToken(){
        String token = jwtProvider.createaccessToken(member);
        assertThat(token).isNotNull();//토큰값이 null값인가
    }

    @Test
    @DisplayName("토큰값 확인")
    public void validToken(){
        String token = jwtProvider.createaccessToken(member);
        assertThat(jwtProvider.getIdFromToken(token)).isEqualTo("1");//토큰에 id값이 일치한가
        assertThat(jwtProvider.getEmailFromToken(token)).isEqualTo("sdf@asd");//토큰 email값이 일치한가
    }

    @Test
    @DisplayName("리프레시로 getMember")
    public void getTokenFromRefreshtoken(){
        String token = jwtProvider.createaccessToken(member);
        memberRepository.save(member);
        assertThat(jwtProvider.getMemberFromRefreshToken("").getProvider_id()).isEqualTo(member.getProvider_id());//리프레시토큰으로 검색이 되는가
    }

    @Test
    @DisplayName("만료된 토큰 확인")
    public void expiredToken(){
        String token = Jwts.builder()
                .setSubject(Long.toString(member.getId()))
                .claim("Authorization", member.getAuthority())
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)), SignatureAlgorithm.HS512)
                .setAudience(member.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()-1))
                .compact();
        assertThatExceptionOfType(ExpiredJwtException.class)
                .isThrownBy(()->
                        jwtProvider.getIdFromToken(token));
    }

    @Test
    @DisplayName("시크릿 키가 틀린 토큰")
    public void tokenError(){
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()->
                        jwtProvider.getIdFromToken(null));
    }

    @Test
    @DisplayName("시크릿 키가 틀린 토큰")
    public void secretKeyFromToken(){
        String token = Jwts.builder()
                .setSubject(Long.toString(member.getId()))
                .claim("Authorization", member.getAuthority())
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret+"asdhfuh")), SignatureAlgorithm.HS512)
                .setAudience(member.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()-1))
                .compact();
        assertThatExceptionOfType(io.jsonwebtoken.security.SecurityException.class)
                .isThrownBy(()->
                        jwtProvider.getIdFromToken(token));
    }
}
