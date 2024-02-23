package com.example.just.Service;

import com.example.just.Dao.Post;
import com.example.just.Document.PostDocument;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MigrationService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostContentESRespository postContentESRespository;

    public void migrationDB(){
        List<Post> dbPosts = postRepository.findAll();
        List<PostDocument> postDocuments = dbPosts.stream()
                .map(PostDocument::new)
                .collect(Collectors.toList());
        postContentESRespository.saveAll(postDocuments);
    }
}
