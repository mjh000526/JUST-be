package com.example.just.jwt;

import com.example.just.Dao.Member;
import com.example.just.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtProvider jwtProvider;

    @Autowired
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken=null;
        String refreshToken=null;
        Authentication authentication;

        //authentication에 정보담기
        accessToken = request.getHeader("accessToken");
        System.out.println("accesstoken:" + accessToken);
        if(accessToken==null||jwtProvider.isTokenExpired(accessToken)){ //액세스토큰이 없거나 만료될 경우
            String email = jwtProvider.getEmailFromToken(accessToken);
            Member member = memberRepository.findByEmail(email);
            refreshToken = request.getHeader("refreshToken");
            if(refreshToken==null||jwtProvider.isRefreshTokenExpired(refreshToken)){//리프레시토큰이 없거나 만료될 경우
                System.out.println("refreshtoken:"+refreshToken);
                String userId = jwtProvider.getIdFromRefreshToken(refreshToken);//리프레시토큰으로 id
                String email = jwtProvider.getEmailFromRefreshToken(refreshToken);
                try{
                    authentication = jwtProvider.authenticate(new UsernamePasswordAuthenticationToken(userId,""));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    HashMap<String, String > m = new HashMap<>();
                    m.put("user_id",userId);
                    m.put("email",email);
                    accessToken = jwtProvider.generateToken(m);
                    response.addHeader("accessToken",accessToken);
                    System.out.println("access 생성");
                }catch (Exception e){
                    System.out.println("액세스토큰오류");
                    throw new RuntimeException(e.getMessage()+"오류발생");
                }
            }
            else if (refreshToken == null||jwtProvider.isRefreshTokenExpired(refreshToken)) { //리프레시토큰이 없거나 만료된 경우
                System.out.println("refresh토큰이 유효하지않음");
                String userId = jwtProvider.getIdFromRefreshToken(refreshToken);
                String email = jwtProvider.getEmailFromRefreshToken(refreshToken);
                try{
                    authentication = jwtProvider.authenticate(new UsernamePasswordAuthenticationToken(userId,""));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    HashMap<String, String > m = new HashMap<>();
                    m.put("user_id",userId);
                    m.put("email",email);
                    refreshToken = jwtProvider.generateRefreshToken(m);
                    accessToken = jwtProvider.generateToken(m);
                    response.setHeader("accessToken",accessToken);
                    response.setHeader("refreshToken",refreshToken);
                }catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }
            }
            else if(accessToken!=null&&!jwtProvider.isTokenExpired(accessToken)){ //액세스토큰이 존재하고 만료되지 않은 경우
                System.out.println("액세스 유효");
                String userId = jwtProvider.getIdFromToken(accessToken);
                String email = jwtProvider.getEmailFromRefreshToken(accessToken);
                System.out.println(userId);
                try {
                    authentication = jwtProvider.authenticate(new UsernamePasswordAuthenticationToken(userId, email));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }catch (Exception e){
                    throw new RuntimeException(e.getMessage());
                }
            }
            filterChain.doFilter(request,response);
        }
    }
}
