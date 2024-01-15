package com.example.just.jwt;

import com.example.just.Dao.Member;
import com.example.just.Repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.SortedMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY="auth";

    public static final String AUTHORIZATION_HEADER = "Authorization";
    @Value("${jwt.secret}")
    private String secret;
    private Key key;
    private final long access_token_time = (1000 * 60) * 60 * 24 * 30L;//60분 * 24 * 30 = 30일
    private final long refresh_token_time = (1000 * 60) * 60 * 24 *3600L;//60분 * 24*30
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }



    //토큰생성
    public String createaccessToken(Member member){
        return Jwts.builder()
                .setSubject(Long.toString(member.getId()))
                .claim(AUTHORITIES_KEY, member.getAuthority())
                .signWith(key,SignatureAlgorithm.HS512)
                .setAudience(member.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + access_token_time))
                .compact();
    }

    public String createRefreshToken(Member member){
        return Jwts.builder()
                .signWith(key,SignatureAlgorithm.HS512)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refresh_token_time))
                .compact();
    }
    public boolean existsRefreshToken(String refreshtoken){
        return memberRepository.existsByRefreshToken(refreshtoken);
    }

    //auth객체 반환
    public Authentication getAuthentication(String token){
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        User principal = new User(claims.getSubject(), claims.getAudience(), authorities);
        return new UsernamePasswordAuthenticationToken(principal,token,authorities);
    }
    //토큰값 가져오기
    public String getAccessToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getRefreshToken(HttpServletRequest request){
        if(request.getHeader("refresh_token")!=null){
            return request.getHeader("refresh_token");
        }
        return null;
    }

    public Member getMemberFromRefreshToken(String refreshToken){
        return memberRepository.findByRefreshToken(refreshToken).get();

    }

    //토큰으로부터 id추출
    public String getIdFromToken(String token){
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    //토큰으로부터 email추출
    public String getEmailFromToken(String token){
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        return claims.getAudience();
    }

    //토큰파싱하고 예외처리
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
