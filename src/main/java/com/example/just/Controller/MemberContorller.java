package com.example.just.Controller;

import com.example.just.Service.AppleService;
import com.example.just.Service.KakaoService;
import com.example.just.Service.MemberService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class MemberContorller {
    @Autowired
    KakaoService kakaoService;
    @Autowired
    AppleService appleService;
    @Autowired
    MemberService memberService;


    @PostMapping("/kakao/login")
    @ApiOperation(value = "카카오 로그인 api", notes = "액세스토큰만 넘기기, 회원가입이 안되어있으면 /api/kakao/signup를 string로 리턴함")
    public ResponseEntity loginKakao(@RequestParam String accessToken) throws IOException{
        return kakaoService.loginKakao(accessToken);
    }

    @PostMapping("/kakao/signup")
    @ApiOperation(value = "카카오 회원가입 api", notes = "닉네임이랑 액세스토큰 같이 넘기기")
    public ResponseEntity signUpKakao(@RequestParam String accessToken,@RequestParam String nickName){
        return kakaoService.signUpKakao(accessToken,nickName);
    }

    @PostMapping("/apple/login")
    @ApiOperation(value = "애플 로그인 api", notes = "identify token값, 회원가입이 안되어있으면 /api/apple/signup를 string로 리턴함")
    public ResponseEntity loginApple(@RequestParam String idToken){
        return appleService.loginApple(idToken);
    }

    @PostMapping("/apple/signup")
    @ApiOperation(value = "애플 회원가입 api", notes = "identify token값 닉네임 값을 파라미터로 줘야함 null이어도 보내줘야함")
    public ResponseEntity signUpApple(@RequestParam String idToken,
                                      @RequestParam String nickName){
        return appleService.signUpApple(idToken,nickName);
    }

    @PostMapping("/nickName")
    @ApiOperation(value = "닉네임 변경 api",notes = "같은 닉네임은 변경 안됨")
    public ResponseEntity changeNickname(HttpServletRequest request, @RequestParam String nickName){
        return kakaoService.changeNickname(request, nickName);
    }

    @PostMapping("/drop")
    @ApiOperation(value = "회원 탈퇴 api", notes = "헤더값에 access_token으로 토큰을 보내줘야함")
    public  ResponseEntity dropUser(HttpServletRequest request){
        return memberService.drop(request);
    }


}
