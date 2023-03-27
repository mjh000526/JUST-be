package com.example.just.Controller;

import com.example.just.Service.AppleService;
import com.example.just.Service.KakaoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class tokenContorller {
    @Autowired
    KakaoService ks;
    @Autowired
    AppleService as;

    @GetMapping("/info")
    public void getInfo(@RequestParam String token) throws IOException {
        ks.getKakaoUser(token);
    }

    @PostMapping("/kakao/login")
    @ApiOperation(value = "카카오 로그인 api", notes = "액세스토큰만 넘기기, 회원가입이 안되어있으면 /api/kakao/signup를 string로 리턴함")
    public ResponseEntity loginKakao(@RequestParam String accessToken) throws IOException{
        return ks.loginKakao(accessToken);
    }

    @PostMapping("/kakao/signup")
    @ApiOperation(value = "카카오 회원가입 api", notes = "닉네임이랑 액세스토큰 같이 넘기기")
    public ResponseEntity signUpKakao(@RequestParam String accessToken,@RequestParam String nickName) throws IOException{
        return ks.signUpKakao(accessToken,nickName);
    }

    @PostMapping("/apple/login")
    @ApiOperation(value = "애플 로그인 api", notes = "email값이 null이어도 보내줘야함, 회원가입이 안되어있으면 /api/apple/signup를 string로 리턴함")
    public ResponseEntity loginApple(@RequestParam String id, @RequestParam String email){
        return as.loginApple(id,email);
    }

    @PostMapping("/apple/signup")
    @ApiOperation(value = "애플 회원가입 api", notes = "고유id값, email값, 닉네임 값을 파라미터로 줘야함 email값이 null이어도 보내줘야함")
    public ResponseEntity signUpApple(@RequestParam String id,@RequestParam String email,@RequestParam String nickname) throws IOException{
        return as.signUpApple(id,email,nickname);
    }
}
