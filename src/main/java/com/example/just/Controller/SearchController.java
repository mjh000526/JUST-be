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
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
                    + "  \"message\": \"로그인 후 검색가능합니다.\", \"페이지를 초과하엿습니다.\", \"해당 내용을 포함하는 게시글이 존재하지 않습니다.\"\n")
    })
    public ResponseEntity getPosts(@RequestParam String keyword,@RequestParam int page, HttpServletRequest request) {
        long beforeTime = System.currentTimeMillis();
        ResponseEntity result = searchService.searchPostContent(request,keyword,page-1);
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - beforeTime; // 밀리초(ms)

        double durationSeconds = durationMillis / 1000.0; // 초(s) 변환
        System.out.println("ES 처리시간 : " + durationSeconds);
        return result;
    }

    @GetMapping("/get/search/tag")
    @Operation(summary = "태그로 게시 검색", description = "해당 태그와 일치하는 값을 가진 게시글 검색\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "{\n"
                    + "  \"message\": \"로그인 후 검색가능합니다.\", \"페이지를 초과하엿습니다.\", \"해당 태그를 가진 게시글이 존재하지 않습니다.\"\n"
                    + "}")
    })
    public ResponseEntity getTagPost(@RequestParam String keyword,@RequestParam int page, HttpServletRequest request) {
        return searchService.searchTagPost(request,keyword,page-1);
    }

    @GetMapping("/get/search/auto/tag")
    @Operation(summary = "태그 자동완성", description = "해당 keyword를 포함하는 태그 전체 검색\n 자음만으로는 검색불가무조건 모음까지 합친 글자로만 검색가능\n ex) ㅇ -> 검색불가\n   연-> 연애,연구")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "{\n"
                    + "  \"message\": \"태그가 존재하지 않습니다.\", \"페이지를 초과하엿습니다.\"\n")
    })
    public ResponseEntity getAutoTag(@RequestParam(required = false, defaultValue = "") String keyword,@RequestParam int page) {
        return searchService.getAutoTag(keyword,page-1);
    }

    // 검색어로 게시글 검색
    @GetMapping("/search/like")
    public Page<Post> searchPostLikeQuery(@RequestParam String keyword) {
        return searchService.searchPostLikeQuery(keyword);
    }
    @GetMapping("/posts/full")
    public List<Post> searchPostFullQuery(HttpServletRequest request, @RequestParam String searchKeyword) {
        List<Post> list = searchService.searchPostFullQuery(searchKeyword);
        return list;
    }
}
