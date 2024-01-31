package com.example.just.Repository;

import com.example.just.Dao.HashTag;
import com.example.just.Dao.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long> {
    List<HashTag> findByPost(Post post_id);
}
