package com.example.just.Repository;

import com.example.just.Dao.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p JOIN p.postContent c WHERE c.content LIKE %?1%")
    Page<Post> searchPostsByContent(String searchKeyword, Pageable pageable);

    // PostContent의 content 필드를 FULLTEXT 검색하는 쿼리
//    @Query(value = "SELECT p.* FROM just.post p JOIN just.post_content c ON p.post_id = c.post_id WHERE MATCH(c.content) AGAINST (?1 IN NATURAL LANGUAGE MODE)", nativeQuery = true)
//    List<Post> searchPostsByFullText(String searchKeyword);

    @Query(value = "SELECT p.* FROM post p " +
            "JOIN post_content c ON p.post_id = c.post_id " +
            "WHERE MATCH(c.content) AGAINST (:searchKeyword IN NATURAL LANGUAGE MODE)", nativeQuery = true)
    List<Post> searchPostsByFullText(@Param("searchKeyword") String searchKeyword);

    @Query(value = "SELECT p.* FROM post p " +
            "JOIN post_content c ON p.post_id = c.post_id " +
            "WHERE MATCH(c.content) AGAINST (:searchKeyword IN NATURAL LANGUAGE MODE) " +
            "LIMIT :pageSize OFFSET :offset", nativeQuery = true)
    List<Post> searchPostsByFullTextWithPagination(
            @Param("searchKeyword") String searchKeyword,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset);



    List<Post> findByBlamedCountGreaterThanEqualOrderByBlamedCountDesc(int blamed_count);
}
