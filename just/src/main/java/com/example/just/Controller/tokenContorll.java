package com.example.just.Controller;

import com.example.just.Service.KakaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/member")
public class tokenContorll {
    @Autowired
    KakaoService ks;

    @GetMapping("/info")
    public void getInfo(@RequestParam String token) throws IOException {
        ks.getKakaoUser(token);
    }

    @GetMapping("/kakao")
    public ResponseEntity getCI(@RequestParam String code, Model model) throws IOException{
        return ks.getToken(code);
    }
}
