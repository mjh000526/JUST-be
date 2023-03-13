package com.example.just.Controller;

import com.example.just.Dao.Post;
import com.example.just.Dto.PostDto;
import com.example.just.Service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RequestMapping("/api")
@RestController

public class PostController {

    @Autowired
    PostService postService;

    @ApiOperation(value = "게시글을 작성하는 API", notes = "<big>게시글을 작성한다</big>")
    @PostMapping("/post/post")
    public Post write(@RequestParam Long member_id,
                      @RequestBody PostDto postDto) {
        return postService.write(member_id, postDto);
    }

    @ApiOperation(value = "게시글을 조회하는 API", notes = "<big>게시글을 조회한다</big>" +
            "랜덤하고 중복되지않게 viewed(이미 읽은 글)라는 헤더에 [1, 2, 3] <-set형식 을 프론트에서 넘겨줘야함" +
            " 백에서 넘겨주니까 로컬스토리지에 저장해놓고 넘겨주면 됨")
    @GetMapping("/get/post")
    public Slice<Post> getPosts(@RequestParam Long request_page,
                                HttpServletRequest req) {
        String cursor = req.getHeader("viewed");
        return postService.searchByCursor(cursor, request_page);
    }


}

