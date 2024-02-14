package com.example.just.Service;

import com.example.just.Document.PostDocument;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    @Autowired
    PostContentESRespository postContentESRespository;

    @Autowired
    PostRepository postRepository;

    public ResponseEntity searchPostContent(String keyword){
        List<PostDocument> searchList = postContentESRespository.findByPostContent_ContentContains(keyword);
        return ResponseEntity.ok(searchList);
    }

}
