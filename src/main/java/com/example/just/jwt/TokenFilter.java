package com.example.just.jwt;

import com.example.just.Dao.Member;
import com.example.just.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

public class TokenFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private TokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    public TokenFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestURI = httpServletRequest.getRequestURI();
        String accessToken = resolveToken(httpServletRequest);
        String refreshToken = httpServletRequest.getHeader("refresh_token");
        if(StringUtils.hasText(accessToken)&& tokenProvider.validateToken(accessToken)){
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("정상작동");
            System.out.println("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}" + authentication.getName() + requestURI);
        }
        else if(!tokenProvider.validateToken(accessToken)&&refreshToken!=null){
            if(tokenProvider.validateToken(refreshToken)&& tokenProvider.existsRefreshToken(refreshToken)){
                System.out.println("토큰터지고 리프레시 있");
                Member member = tokenProvider.getMemberFromRefreshToken(refreshToken);
                String newToken = tokenProvider.createaccessToken(member);
                HttpHeaders httpHeaders = new HttpHeaders();
                ((HttpServletResponse) response).setHeader(TokenFilter.AUTHORIZATION_HEADER,"Bearer " + newToken);
                Authentication authentication = tokenProvider.getAuthentication(newToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }else{
            System.out.println("유효한 JWT 토큰이 없습니다, uri: {}" + requestURI);
        }
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken)&&bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
