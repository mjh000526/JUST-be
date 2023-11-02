package com.example.just.Controller;


import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.example.just.Service.PostService;
import com.example.just.Service.ResponseGetPost;
import com.example.just.Service.ResponsePost;
import com.example.just.jwt.JwtProvider;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
        String token = jwtProvider.getAccessToken(req);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰

        String cursor = req.getHeader("viewed");

        return postService.searchByCursor(cursor, request_page, member_id);
    }

    /*
    @Operation(summary =  "자기의 게시글을 조회하는 API", description =  "<big> 자신의 게시글을 조회한다</big>")
    @GetMapping("/get/mypost")
    public ResponseEntity<Slice<Post>> getMyPosts(
            @RequestParam Long request_page,
            HttpServletRequest request) {

        String token = request.getHeader("access_token");
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        Slice<Post> postSlice = postService.searchByMyPost(request_page, member_id);
        return ResponseEntity.ok().body(postSlice);
    }

     */

    @Operation(summary = "게시글 작성 api", description = "RequestBody에 null값이 있으면 안됨"
            + "\n 공개글이면 true 아니라면 false")
    @PostMapping("/post/post")
    public ResponsePost write(HttpServletRequest request,
                              @RequestBody PostPostDto post_dto) {
        String token = jwtProvider.getAccessToken(request);
        System.out.println(token + "ㅋㅋ");
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰

        return postService.write(member_id, post_dto);
    }

    @Operation(summary = "게시글 삭제 api", description = "\n 자기가 지운 글이면 true")
    @DeleteMapping("/delete/post")
    public ResponsePost deletePost(@RequestParam Long post_id) {
        return postService.deletePost(post_id);
    }

    @Operation(summary = "게시글 수정 api", description = "JSON넘길 때 null이 하나도 있으면 안됨 꼭 다채워서 넘기기")
    @PutMapping("/put/post")
    public ResponsePost putPost(HttpServletRequest request,
                                @RequestBody PutPostDto post_dto) {
        String token = jwtProvider.getAccessToken(request);

        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        return postService.putPost(member_id, post_dto);
    }

    @Operation(summary = "게시글 좋아요 api", description = "\n 자기가 좋아요 한 글이면 true")
    @PostMapping("/post/like")
    public ResponseEntity<Void> postLikes(@RequestParam Long post_id, HttpServletRequest request) {
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        postService.postLikes(post_id, member_id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "게시글 신고")
    @PostMapping("/post/blame/post")
    public ResponseEntity<String> blamePost(@RequestParam Long post_id) {
        return postService.blamePost(post_id);
    }

    @ApiOperation(value = "댓글 신고 횟수 조회")
    @GetMapping("/get/post/blame/{post_id}")
    public int blameGetComment(@PathVariable Long post_id) {
        return postService.blameGetPost(post_id);
    }
}

