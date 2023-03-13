package com.example.just.Controller;

import com.example.just.Service.KakaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Controller
@RequestMapping("/member")
public class KaKaoController {
    @Autowired
    KakaoService ks;

    @GetMapping("/do")
    public String loginPage(){
        return "kakaoCI/login";
    }



}
