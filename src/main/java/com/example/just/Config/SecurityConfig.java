package com.example.just.Config;
import com.example.just.Service.KakaoService;
import com.example.just.jwt.JwtAccessDeniedHandler;
import com.example.just.jwt.JwtAuthenticationEntryPoint;
import com.example.just.jwt.JwtProvider;
import com.example.just.jwt.JwtSecurityConfig;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired private KakaoService kakaoService;
    @Autowired
    private final JwtProvider jwtProvider;
    @Autowired
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private String[] permitList={
            "/v2/**",
            "/v3/**",
            "/configuration",
            "/swagger*/**",
            "/webjars/**",
            "/swagger-resources/**"
    };

    //jwt 인스턴스 등록
    public SecurityConfig(
            JwtProvider jwtProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ){
        this.jwtProvider = jwtProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    //jwt암호 인코더
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer2() {  //해당 URL은 필터 거치지 않겠다
        return (web -> web.ignoring().antMatchers("/api/**", "/h2-console","/swagger/**","/swagger-ui/**"));

        //return (web -> web.ignoring().antMatchers("/test"));
    }

    //클라이언트측 접근을 허가하는 cors 설정
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                Arrays.asList("http://localhost:3000", "https://s3-ap-northeast-2.amazonaws.com", "https://github.com", "http://13.209.213.191:9000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("viewed", "Origin", "X-Requested-With", "Content-Type", "Accept", "Key", "Authorization", "access-control-allow-origin", "Authorizationsecret", "accessToken", "refreshToken"));
        configuration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }


    //각 api 요청에 대한 클라이언트의 권한 확인 및 허가
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(corsFilter(), CorsFilter.class)
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/**").permitAll()
                .antMatchers("/admin/**").permitAll()
                .antMatchers(permitList).permitAll()
                .antMatchers("/test/**").hasRole("USER")
                .anyRequest().authenticated()
                .and()
                .apply(new JwtSecurityConfig(jwtProvider));
                //.anyRequest().permitAll()
    }
}
