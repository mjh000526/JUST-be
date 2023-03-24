package com.example.just.Controller;

import com.example.just.Service.KakaoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
<<<<<<< HEAD:src/main/java/com/example/just/Controller/tokenContorller.java
public class tokenContorller {
=======
public class tokenContorll {
>>>>>>> develop:src/main/java/com/example/just/Controller/tokenContorll.java
    @Autowired
    KakaoService ks;

    @GetMapping("/info")
    public void getInfo(@RequestParam String token) throws IOException {
        ks.getKakaoUser(token);
    }

    @GetMapping("/kakao")
    @ApiOperation(value = "로그인 api", notes = "카카오 인가코드만 넘겨주면 jwt토큰 발급")
    public ResponseEntity getCI(@RequestParam String code) throws IOException{
        return ks.getToken(code);
    }
}
