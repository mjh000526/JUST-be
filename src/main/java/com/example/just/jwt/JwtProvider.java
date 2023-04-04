package com.example.just.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.just.Service.MyUserDetailsService;
import com.example.just.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.sql.Ref;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtProvider implements AuthenticationProvider {


    private final MyUserDetailsService myUserDetailsService;

    public final long AccessTokenTime = (1000*60)*60000000;//액세스60분
    public final long RefreshTokenTime = (1000*60)*12000000;//액세스120분


    @Value("${spring:jwt:secret}")
    private String SECRET_KEY;

    private String ISSUER;

    private Algorithm getSignKey(String secretKey){
        return Algorithm.HMAC256(secretKey);
    }

    //액세스로부터 id
    public String getIdFromToken(String token){
        DecodedJWT verifiedToken = validateToken(token);
        return verifiedToken.getClaim("user_id").asString();
    }

    //액세스로부터 email
    public String getEmailFromToken(String token){
        DecodedJWT verifiedToken = validateToken(token);
        return verifiedToken.getClaim("email").asString();
    }

    //리프레시로부터 id
    public String getIdFromRefreshToken (String token){
        DecodedJWT verifiedToken = validateRefreshToken(token);
        return verifiedToken.getClaim("id").asString();
    }
    //리프레시로부터 email
    public String getEmailFromRefreshToken(String token){
        DecodedJWT verifiedToken = validateToken(token);
        return verifiedToken.getClaim("email").asString();
    }

    private JWTVerifier getTokenValidator(){
        return JWT.require(getSignKey(SECRET_KEY))
                .acceptExpiresAt(Math.max(0, AccessTokenTime))
                .build();
    }

    private JWTVerifier getRefreshTokenValidator(){
        return JWT.require(getSignKey(SECRET_KEY))
                .withIssuer(ISSUER)
                .acceptExpiresAt(Math.max(0, AccessTokenTime))
                .build();
    }

    public String generateToken(Map<String, String> payload){
        return doGenerateToken(AccessTokenTime,payload);
    }

    public String generateRefreshToken(Map<String, String> payload){
        return doGenerateToken(RefreshTokenTime,payload);
    }

    public String doGenerateToken(long expireTime, Map<String, String>payload){
        return JWT.create()
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + expireTime))
                .withPayload(payload)
                .sign(getSignKey(SECRET_KEY));
    }

    private DecodedJWT validateToken(String token) throws JWTVerificationException{
        JWTVerifier validator = getTokenValidator();
        return validator.verify(token);
    }

    private DecodedJWT validateRefreshToken(String token) throws JWTVerificationException{
        JWTVerifier validator = getRefreshTokenValidator();
        return validator.verify(token);
    }

    public boolean isTokenExpired(String token){
        try{
            DecodedJWT decodedJWT = validateToken(token);
            return false;
        }catch (JWTVerificationException e){
            return true;
        }
    }

    public boolean isRefreshTokenExpired(String token){
        try{
            DecodedJWT decodedJWT = validateRefreshToken(token);
            return false;
        } catch(JWTVerificationException e){
            return true;
        }
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        PrincipalDetails user = (PrincipalDetails) myUserDetailsService.loadUserByUsername((String) authentication.getPrincipal());
        return new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
