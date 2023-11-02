package com.example.just.Controller;

import com.example.just.Service.KakaoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api(tags = {"test controller"}, description = "카카오 oauth테스트")
@RestController
@RequestMapping("/api")
public class MemberTestController {
    @Autowired
    KakaoService ks;

    @GetMapping("/kakao/access_token")
    @ApiOperation(value = "카카오 토큰받기",notes = "이건 직접호출할 필요없이 member/do로 버튼누르면 실행됨\n" +
            " 이토큰을 받고 /api/kakao/testsign <-- 회원가입\n" +
            "/api/kakao/testlogin <== 로그인으로 사용하면됨")
    public String testLogin(@RequestParam String code, Model model) throws IOException {
        return ks.getToken(code);
    }

    @PostMapping("/kakao/testsign")
    @ApiOperation(value = "카카오 토큰으로 회원가입")
    public ResponseEntity signUpKakao(@RequestParam String access_token){
        return ks.signUpKakao(access_token,"테스트용");
    }

    @PostMapping("/kakao/testlogin")
    @ApiOperation(value = "카카오 토큰으로 로그인")
    public ResponseEntity loginKakao(@RequestParam String access_token) throws IOException{
        return ks.loginKakao(access_token);
    }
}
