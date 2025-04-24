package com.example.just.Controller;

import com.example.just.BasicResponse;
import com.example.just.Dto.MemberDto;
import com.example.just.Service.KakaoService;
import com.example.just.Service.MemberService;
import com.example.just.Service.PostService;
import com.example.just.jwt.JwtProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/test")
@Api(tags = {"test controller"}, description = "회원 정보 조회 테스트")
@RestController
public class BackDummyController {


    @Autowired
    MemberService memberService;

    @Autowired
    KakaoService kakaoService;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    PostService postService;

    @PostMapping("/post/member")
    @ApiOperation(value = "회원가입 테스트용", notes = "테스트용으로 사용")
    public ResponseEntity<BasicResponse> join(@RequestBody MemberDto member_dto) {
        return memberService.join(member_dto);
    }

    @GetMapping("/info")
    public ResponseEntity getInfo() throws IOException {
        return kakaoService.info();
    }

    @PostMapping("/test/dataset")
    public ResponseEntity insertDataset(HttpServletRequest request){
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        return postService.insertDataset(member_id);
    }
}
