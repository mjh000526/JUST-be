package com.example.just.jwt;

import com.example.just.Dao.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestURI = httpServletRequest.getRequestURI();
        String accessToken = httpServletRequest.getHeader("access_token");
        String refreshToken = httpServletRequest.getHeader("refresh_token");
        if(StringUtils.hasText(accessToken)&& jwtProvider.validateToken(accessToken)){
            Authentication authentication = jwtProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}" + authentication.getName() + requestURI);
        }
        else if(!jwtProvider.validateToken(accessToken)&&refreshToken!=null){
            if(jwtProvider.validateToken(refreshToken)&& jwtProvider.existsRefreshToken(refreshToken)){
                Member member = jwtProvider.getMemberFromRefreshToken(refreshToken);
                String newToken = jwtProvider.createaccessToken(member);
                httpServletResponse.setHeader("access_token",newToken);
                Authentication authentication = jwtProvider.getAuthentication(newToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }else{
            System.out.println("유효한 JWT 토큰이 없습니다, uri: {}" + requestURI);
        }
        chain.doFilter(request, response);
    }
}
