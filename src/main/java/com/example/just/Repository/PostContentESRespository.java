package com.example.just.Repository;
import com.example.just.Document.PostDocument;
import org.elasticsearch.search.SearchHits;
import org.h2.mvstore.Page;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostContentESRespository extends ElasticsearchRepository<PostDocument,Long>,
        CrudRepository<PostDocument,Long> {
//    List<PostDocument> findByPostContent_ContentContains(String text);
//        List<PostDocument> findByPostContent_ContentContains(String text);

    //해당 text문자열을 하나라도 포함하는 게시글 전제조회(ELK)
    List<PostDocument> findByPostContentContaining(String text);

    //해당 text와 일치하는 태그값을 가지는 Post값 전체 조회(ELK)
    List<PostDocument> findByHashTagIn(String text);

}

