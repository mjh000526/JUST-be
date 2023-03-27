package com.example.just.Service;

import com.example.just.Dao.Member;
import com.example.just.Dao.Role;
import com.example.just.Dto.TokenDto;
import com.example.just.Repository.MemberRepository;
import com.example.just.jwt.JwtProvider;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AppleService {
    @Autowired
    private MemberRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    public ResponseEntity loginApple(String id,String email){
        Member user = userRepository.findByEmail(email);
        if(user != null){
            return new ResponseEntity<>("이미 회원가입되어있는 이메일입니다.", HttpStatus.OK);
        }
        else if(email != null){
            return new ResponseEntity<>("/api/apple/signup", HttpStatus.OK);
        }
        HashMap<String,String> m = new HashMap<>();
        m.put("user_id",user.getId().toString());
        m.put("email",user.getEmail().toString());
        //jwt토큰생성
        String accesstoken = jwtProvider.generateToken(m);
        String refreshtoken = jwtProvider.generateRefreshToken(m);
        return new ResponseEntity<>(new TokenDto(accesstoken,refreshtoken), HttpStatus.OK);
    }

    public ResponseEntity signUpApple(String id,String email,String nickname){
        Member user = Member.builder()
                .email(email)
                .provider("apple")
                .provider_id(id)
                .nickname(nickname)
                .role(Role.ROLE_USER)
                .blameCount(0)
                .blamedCount(0)
                .build();
        userRepository.save(user);
        HashMap<String,String> m = new HashMap<>();
        m.put("user_id",user.getId().toString());
        m.put("email",user.getEmail());

        //jwt토큰생성
        String accesstoken = jwtProvider.generateToken(m);
        String refreshtoken = jwtProvider.generateRefreshToken(m);
        return new ResponseEntity<>(new TokenDto(accesstoken,refreshtoken), HttpStatus.OK);
    }
}
