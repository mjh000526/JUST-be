package com.example.just.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private JwtProvider jwtProvider;
    public JwtSecurityConfig(JwtProvider jwtProvider){
        this.jwtProvider = jwtProvider;
    }
    @Override
    public void configure(HttpSecurity http){//tokenProvider를 주입받아서 JwtFilter를 통해 security에 등록
        JwtFilter customFilter = new JwtFilter(jwtProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
