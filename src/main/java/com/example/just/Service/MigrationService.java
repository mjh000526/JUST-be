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
        System.out.println("디비값"+dbPosts.size());
        List<PostDocument> postDocuments = dbPosts.stream()
                .map(PostDocument::new)
                .collect(Collectors.toList());
        System.out.println("객체는 만들어짐" + postDocuments.size());
        postContentESRespository.saveAll(postDocuments);
    }
}
