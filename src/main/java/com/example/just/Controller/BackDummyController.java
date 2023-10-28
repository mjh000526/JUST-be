package com.example.just.Controller;

import com.example.just.BasicResponse;
import com.example.just.Dto.MemberDto;
import com.example.just.Service.KakaoService;
import com.example.just.Service.MemberService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/test")
@RestController
public class BackDummyController {


    @Autowired
    MemberService memberService;

    @Autowired
    KakaoService kakaoService;

    @PostMapping("/post/member")
    @ApiOperation(value = "회원가입 테스트용", notes = "테스트용으로 사용")
    public ResponseEntity<BasicResponse> join(@RequestBody MemberDto memberDto) {
        return memberService.join(memberDto);
    }

    @GetMapping("/info")
    public ResponseEntity getInfo() throws IOException {
        return kakaoService.info();
    }
}
