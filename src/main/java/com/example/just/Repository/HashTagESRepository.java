package com.example.just.Repository;

import com.example.just.Document.HashTagDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashTagESRepository extends ElasticsearchRepository<HashTagDocument,Long>,
        CrudRepository<HashTagDocument,Long> {
    List<HashTagDocument> findByNameContaining(String name);

}