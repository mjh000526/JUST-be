package com.example.just.Controller;

import com.example.just.Service.ImageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    @Autowired
    private ImageService imageService;


    @GetMapping("/{filename}")
    @ApiOperation(value = "이미지 url 가져오기", notes = "이미지 url 가져오기")
    public ResponseEntity<Object> getImageUrl(@PathVariable String filename) {
        String url = imageService.getPresignedUrl(filename);
        return ResponseEntity.ok(url);
    }
}
