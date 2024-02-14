package com.example.just.Controller;

import com.example.just.Document.PostDocument;
import com.example.just.Service.SearchService;
import com.example.just.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
@Tag(name = "search", description = "검색 api")
public class SearchController {
    @Autowired
    SearchService searchService;

    @Autowired
    JwtProvider jwtProvider;

    @GetMapping("/get/search/post")
    public ResponseEntity<?> getPosts(@RequestParam String keyword) {
        return searchService.searchPostContent(keyword);
    }
}
