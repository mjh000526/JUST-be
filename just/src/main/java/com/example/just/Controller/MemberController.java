package com.example.just.Controller;

import com.example.just.BasicResponse;
import com.example.just.Dto.MemberDto;
import com.example.just.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class MemberController {


    @Autowired
    MemberService memberService;

    @PostMapping("/post/member")
    public ResponseEntity<BasicResponse> join(@RequestBody MemberDto memberDto) {
        return memberService.join(memberDto);
    }
}
