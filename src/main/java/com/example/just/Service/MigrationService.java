package com.example.just.Service;

import com.example.just.Dao.HashTag;
import com.example.just.Dao.Post;
import com.example.just.Document.HashTagDocument;
import com.example.just.Document.PostDocument;
import com.example.just.Repository.HashTagESRepository;
import com.example.just.Repository.HashTagRepository;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MigrationService {//ELK와 mysql 동기화 서비스

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HashTagRepository hashTagRepository;

    @Autowired
    private PostContentESRespository postContentESRespository;

    @Autowired
    private HashTagESRepository hashTagESRepository;

    public void migrationDB(){
        //게시글 테이블 전체 조회
        List<Post> dbPosts = postRepository.findAll();

        //게시글 리스트를 ELK 포맷으로 변환
        List<PostDocument> postDocuments = dbPosts.stream()
                .map(PostDocument::new)
                .collect(Collectors.toList());
        //ELK에 게시글 전체 저장
        postContentESRespository.saveAll(postDocuments);
        //태그 테이블 전체 조회
        List<HashTag> dbTag = hashTagRepository.findAll();
        //태그 리스트를 ELK 포맷으로 변환
        List<HashTagDocument> hashTagDocuments = dbTag.stream()
                .map(HashTagDocument::new)
                .collect(Collectors.toList());
        //ELK에 태그 전체 저장
        hashTagESRepository.saveAll(hashTagDocuments);
    }
}
