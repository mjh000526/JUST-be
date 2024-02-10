package com.example.just.Controller;


import com.example.just.Dto.*;
import com.example.just.Service.PostService;
import com.example.just.Service.ResponseGetPost;
import com.example.just.Service.ResponsePost;
import com.example.just.jwt.JwtProvider;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RequestMapping("/api")
@RestController
@Tag(name = "Post", description = "게시글 관련 api")
public class PostController {

    @Autowired
    PostService postService;

    @Autowired
    JwtProvider jwtProvider;

    @Operation(summary = "게시글 랜덤하게 조회 api", description = "<big>게시글을 조회한다</big>" +
            "랜덤하고 중복되지않게 viewed(이미 읽은 글)라는 헤더에 [1, 2, 3] <-set형식 을 프론트에서 넘겨줘야함" +
            " 백에서 넘겨주니까 로컬스토리지에 저장해놓고 넘겨주면 됨\n 자기 글이 조회되면  true")
    @GetMapping("/get/post")
    public ResponseGetPost getPosts(@RequestParam Long request_page,
                                    HttpServletRequest req) {
        String cursor = req.getHeader("viewed");

        return postService.searchByCursor(cursor, request_page);
    }


    @Operation(summary = "자기의 게시글을 조회하는 API", description = "<big> 자신의 게시글을 조회한다</big>")
    @GetMapping("/get/mypost")
    public List<ResponseGetMemberPostDto> getMyPosts(HttpServletRequest request) {

        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        return postService.getMyPost(member_id);
    }


    @Operation(summary = "게시글 랜덤하게 조회(회원용) api", description = "자기가 좋아요한 글을 조회했다면"
            + "\n like : true 아니라면 like : false 이다.")
    @GetMapping("/get/member/post")
    public ResponseGetPost getMemberPosts(@RequestParam Long request_page, HttpServletRequest request) {
        String cursor = request.getHeader("viewed");
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        return postService.searchByCursorMember(cursor, request_page, member_id);
    }

    @Operation(summary = "게시글 작성 api", description = "{\n"
            + "  \"hash_tage\": [\n"
            + "    \"바보\", \"멍청이\""
            + "  ],\n"
            + "  \"post_content\": [\n"
            + "    \"오늘은 2월 8일 입니다.\", \"내일은 휴가입니다."
            + "  ],\n"
            + "  \"post_picture\": 0,\n"
            + "  \"secret\": true\n"
            + "}"
            + "이런식으로 작성하면 됩니다.")
    @PostMapping("/post/post")
    public PostPostDto write(HttpServletRequest request,
                             @RequestBody PostPostDto postDto) {
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰

        return postService.write(member_id, postDto);
    }

    @Operation(summary = "게시글 삭제 api", description = "\n 글이 삭제되면 value : 삭제 완료"
            + "\n 글이 없으면 value : 글이 없습니다.")
    @GetMapping("/delete/post")
    public ResponsePost deletePost(@RequestParam Long post_id) {
        return postService.deletePost(post_id);
    }

    @Operation(summary = "게시글 수정 api", description = "JSON넘길 때 null이 하나도 있으면 안됨 꼭 다채워서 넘기기")
    @PutMapping("/put/post")
    public ResponsePutPostDto putPost(HttpServletRequest request,
                                      @RequestBody PutPostDto postDto) {
        String token = jwtProvider.getAccessToken(request);

        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        return postService.putPost(member_id, postDto);
    }

    @Operation(summary = "게시글 좋아요 api", description = "자기가 이 글이 좋아요를 누른거면 Response의 value: 좋아요 완료"
            + " 좋아요를 취소한거면 value: 좋아요 취소")
    @PostMapping("/post/like")
    public ResponseEntity postLikes(@RequestParam Long post_id,
                                    HttpServletRequest request) {
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        return postService.postLikes(post_id, member_id);
    }

    @ApiOperation(value = "게시글 신고")
    @PostMapping("/post/blame/post")
    public Long blamePost(@RequestParam Long post_id) {
        return postService.blamePost(post_id);
    }

    @ApiOperation(value = "댓글 신고 횟수 조회")
    @GetMapping("/get/post/blame/{postId}")
    public int blameGetComment(@PathVariable Long postId) {
        return postService.blameGetPost(postId);
    }

    @ApiOperation(value = "자신이 좋아요한 글 조회")
    @GetMapping("/get/like/member/post")
    public List<ResponseGetMemberPostDto> getLikeMemberPost(HttpServletRequest request) {
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        return postService.getLikeMemberPost(member_id);
    }
}

