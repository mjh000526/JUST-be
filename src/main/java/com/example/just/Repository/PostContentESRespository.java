package com.example.just.Repository;
import com.example.just.Document.PostDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostContentESRespository extends ElasticsearchRepository<PostDocument,Long>,
        CrudRepository<PostDocument,Long> {
//    List<PostDocument> findByPostContentContaining(String text);

    @Query("{\"match\": {\"post_content\": {\"query\": \"?0\", \"operator\": \"and\"}}}")
    Page<PostDocument> searchByPostContentMatch(String text, Pageable pageable);

    List<PostDocument> findByHashTagIn(String text);
}