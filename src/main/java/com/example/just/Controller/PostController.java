package com.example.just.Controller;


import com.example.just.Dto.PostPostDto;
import com.example.just.Dto.PutPostDto;
import com.example.just.Dto.ResponseGetPostDto;
import com.example.just.Dto.ResponsePutPostDto;
import com.example.just.Service.PostService;
import com.example.just.Service.ResponseGetPost;
import com.example.just.Service.ResponsePost;
import com.example.just.jwt.JwtProvider;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

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


    @Operation(summary =  "자기의 게시글을 조회하는 API", description =  "<big> 자신의 게시글을 조회한다</big>")
    @GetMapping("/get/mypost")
    public List<ResponseGetPostDto> getMyPosts(HttpServletRequest request) {

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
    @Operation(summary = "게시글 작성 api", description = "[\"오늘은 날이 참 좋네요\"], [\"내일도 좋겠다\"] . . . (List형식)"
            + "이런 형식으로 post_content 작성 해주세요\n post_picture 은 어떤 사진을 저장했는지 알기위해 저장합니다\n"
            + "예를들어 1이면 1번사진 2면 2번사진")
    @PostMapping("/post/post")
    public PostPostDto write(HttpServletRequest request,
                             @RequestBody PostPostDto postDto) {
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰

        return postService.write(member_id, postDto);
    }

    @Operation(summary = "게시글 삭제 api", description = "\n 자기가 지운 글이면 true")
    @DeleteMapping("/delete/post")
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

    @Operation(summary = "게시글 좋아요 api", description = "자기가 이 글이 좋아요를 누른거면 Response의 value는 true 좋아요를 취소한거면 value는 false")
    @PostMapping("/post/like")
    public ResponsePost postLikes(@RequestParam Long post_id, HttpServletRequest request) {
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
    public List<Long> getLikeMemberPost(HttpServletRequest request) {
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        return postService.getLikeMemberPost(member_id);
    }
}

