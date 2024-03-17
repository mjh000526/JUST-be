package com.example.just.Repository;

import com.example.just.Dao.HashTag;
import com.example.just.Dao.HashTagMap;
import com.example.just.Dao.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashTagMapRepository extends JpaRepository<HashTagMap, Long> {
    void deleteByHashTagId(Long id);
}
