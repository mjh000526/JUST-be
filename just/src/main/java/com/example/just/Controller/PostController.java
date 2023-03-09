package com.example.just.Controller;

import com.example.just.BasicResponse;

import com.example.just.Dto.PostDto;
import com.example.just.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RequestMapping("/api")
@RestController
public class PostController {

    @Autowired
    PostService postService;

    @PostMapping("/post/post")
    public ResponseEntity<BasicResponse>  write(@RequestParam Long member_id,
                                                @RequestBody PostDto postDto) {
        return postService.write(member_id, postDto);
    }
}
