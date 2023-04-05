package com.example.just.Controller;

import com.example.just.Dao.Post;
import com.example.just.Dto.PostDto;
import com.example.just.Service.PostService;
import com.example.just.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Slice;
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
            " 백에서 넘겨주니까 로컬스토리지에 저장해놓고 넘겨주면 됨")
    @GetMapping("/get/post")
    public Slice<Post> getPosts(@RequestParam Long request_page,
                                HttpServletRequest req) {
        String cursor = req.getHeader("viewed");
        return postService.searchByCursor(cursor, request_page);
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

    @Operation(summary = "게시글 작성 api", description = "post_content, post_tag는 null값이 발생하면" +
            " 안됨\n" + "다른 건 null이와도 예외처리 완료")
    @PostMapping("/post/post")
    public Post write(HttpServletRequest request,
                      @RequestBody PostDto postDto) {
        String token = request.getHeader("access_token");
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰

        return postService.write(member_id, postDto);
    }

    @Operation(summary = "게시글 삭제 api")
    @DeleteMapping("/delete/post")
    public String deletePost(@RequestParam Long post_id) {
        return postService.deletePost(post_id);
    }

    @Operation(summary = "게시글 수정 api", description = "JSON넘길 때 null이 하나도 있으면 안됨 꼭 다채워서 넘기기")
    @PutMapping("/put/post")
    public Post putPost(@RequestParam Long post_id, HttpServletRequest request,
                        @RequestBody PostDto postDto) {
        String token = request.getHeader("access_token");
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        return postService.putPost(post_id, member_id, postDto);
    }

    @Operation(summary = "게시글 좋아요 api")
    @PostMapping("/post/like")
    public ResponseEntity<Void> postLikes(@RequestParam Long post_id,HttpServletRequest request) {
        String token = request.getHeader("access_token");
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        postService.postLikes(post_id, member_id);
        return ResponseEntity.ok().build();
    }





}

