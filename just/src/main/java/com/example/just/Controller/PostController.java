package com.example.just.Controller;

import com.example.just.Dao.Post;
import com.example.just.Dto.PostDto;
import com.example.just.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RequestMapping("/api")
@RestController
public class PostController {

    @Autowired
    PostService postService;

    @PostMapping("/post/post")
    public Post write(@RequestParam Long member_id,
                      @RequestBody PostDto postDto) {
        return postService.write(member_id, postDto);
    }


    @GetMapping("/get/post")
    public Slice<Post> getPosts(@RequestParam Long request_page,
                                HttpServletRequest req) {
        String cursor = req.getHeader("viewed");
        return postService.searchByCursor(cursor, request_page);
    }


}
