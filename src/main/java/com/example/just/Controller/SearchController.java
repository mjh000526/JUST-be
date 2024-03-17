package com.example.just.Controller;

import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Service.SearchService;
import com.example.just.jwt.JwtProvider;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@Api(tags = {"search controller"},description = "검색 관련 api")
@RestController
public class SearchController {
    @Autowired
    SearchService searchService;

    @Autowired
    JwtProvider jwtProvider;

    @GetMapping("/get/search/post")
    @Operation(summary = "게시글 내용 검색", description = "해당 keyword를 content에 포함하는 게시글 검색\n태그검색구현시 추후 변경 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "{\n"
                    + "  \"message\": \"로그인 후 검색가능합니다.\"\n"
                    + "}")
    })
    public ResponseEntity getPosts(@RequestParam String keyword,@RequestParam int page, HttpServletRequest request) {
        return searchService.searchPostContent(request,keyword,page-1);
    }

    @GetMapping("/get/search/tag")
    @Operation(summary = "태그로 게시 검색", description = "해당 태그와 일치하는 값을 가진 게시글 검색\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "{\n"
                    + "  \"message\": \"로그인 후 검색가능합니다.\"\n"
                    + "}")
    })
    public ResponseEntity getTagPost(@RequestParam String keyword,@RequestParam int page, HttpServletRequest request) {
        return searchService.searchTagPost(request,keyword,page-1);
    }

    @GetMapping("/get/search/auto/tag")
    @Operation(summary = "태그 자동완성", description = "해당 keyword를 포함하는 태그 전체 검색\n 자음만으로는 검색불가무조건 모음까지 합친 글자로만 검색가능\n ex) ㅇ -> 검색불가\n   연-> 연애,연구")
    public ResponseEntity getAutoTag(@RequestParam(required = false, defaultValue = "") String keyword) {
        return searchService.getAutoTag(keyword);
    }
}