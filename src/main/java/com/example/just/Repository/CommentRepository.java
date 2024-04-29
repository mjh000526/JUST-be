package com.example.just.Repository;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    //신고받은 횟수를 기준으로 오름차순으로 검색
    List<Comment> findByBlamedCountGreaterThanEqualOrderByBlamedCountDesc(int blame_count);

    //해당 멤버가 작성한 댓글 전체 조회
    List<Comment> findAllByMember(Member member);
}
