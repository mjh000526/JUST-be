package com.example.just.Repository;

import com.example.just.Document.HashTagDocument;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashTagESRepository extends ElasticsearchRepository<HashTagDocument, Long>,
        CrudRepository<HashTagDocument, Long> {

    //해당 name을 포함하는 태그 조회(ELK)
    List<HashTagDocument> findByNameContaining(String name, Sort sort);

    //모든 태그 조회(ELK)
    List<HashTagDocument> findAll(Sort sort);

}