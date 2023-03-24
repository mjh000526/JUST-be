package com.example.just.Repository;

import com.example.just.Dao.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBlamedCountGreaterThanEqualOrderByBlamedCountDesc(int blame_count);
}
