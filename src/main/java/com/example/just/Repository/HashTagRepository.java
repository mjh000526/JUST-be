package com.example.just.Repository;

import com.example.just.Dao.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long> {
    HashTag findByName(String s);
    // List<HashTag> findByPost(Post post_id);
}