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
    public ResponseEntity loginKakao(@RequestParam String access_token) throws IOException{
        return kakaoService.loginKakao(access_token);
    }

    @PostMapping("/kakao/signup")
    @ApiOperation(value = "카카오 회원가입 api", notes = "닉네임이랑 액세스토큰 같이 넘기기")
    public ResponseEntity signUpKakao(@RequestParam String access_token,@RequestParam String nickname){
        return kakaoService.signUpKakao(access_token,nickname);
    }

    @PostMapping("/apple/login")
    @ApiOperation(value = "애플 로그인 api", notes = "identify token값, 회원가입이 안되어있으면 /api/apple/signup를 string로 리턴함")
    public ResponseEntity loginApple(@RequestParam String id_token){
        return appleService.loginApple(id_token);
    }

    @PostMapping("/apple/signup")
    @ApiOperation(value = "애플 회원가입 api", notes = "identify token값 닉네임 값을 파라미터로 줘야함 null이어도 보내줘야함")
    public ResponseEntity signUpApple(@RequestParam String id_token,
                                      @RequestParam String nickname){
        return appleService.signUpApple(id_token,nickname);
    }

    @PostMapping("/change/nickName")
    @ApiOperation(value = "닉네임 변경 api",notes = "같은 닉네임은 변경 안됨")
    public ResponseEntity changeNickname(HttpServletRequest request, @RequestParam String nickname){
        return kakaoService.changeNickname(request, nickname);
    }

    @PostMapping("/change/refresh")
    @ApiOperation(value = "리프레시 교체 api", notes = "리프레시만 주면 db값 바꾸고 액세스,리프레시 재발급")
    public ResponseEntity changeRefresh(HttpServletRequest request){
        return memberService.changeRefresh(request);
    }
    @PostMapping("/drop")
    @ApiOperation(value = "회원 탈퇴 api", notes = "헤더값에 access_token으로 토큰을 보내줘야함")
    public  ResponseEntity dropUser(HttpServletRequest request){
        return memberService.drop(request);
    }


}
