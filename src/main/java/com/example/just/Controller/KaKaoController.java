package com.example.just.Controller;

import com.example.just.Service.KakaoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@Api(tags = {"test controller"}, description = "카카오 액세스 받기")
@RequestMapping("/api")
public class KaKaoController {
    @Autowired
    KakaoService ks;

    @GetMapping("/member/do")
    @ApiOperation(value = "카카오로그인", notes = "이것도 테스트용으로 카카오로그인할 수 있게 구현")
    public String loginPage(){
        return "kakaoCI/login";
    }

}
