package com.example.just.Repository;

import com.example.just.Dao.Blame;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlameRepository extends JpaRepository<Blame, Long> {

    //blameMember 회원이 targetMemberId 회원을 신고했는지 여부
    boolean existsByBlameMemberIdAndTargetMemberId(Long blameMemberId,Long targetMemberId);

    //blameMember 회원이 targetMemberId 회원을 신고한 이력 조회
    Optional<Blame> findByBlameMemberIdAndTargetMemberId(Long blameMemberId, Long targetCommentId);

    //blameMember 회원이 targetPostId 게시글을 신고했는지 여부
    boolean existsByBlameMemberIdAndTargetPostId(Long blameMemberId, Long targetPostId);

    //blameMember 회원이 targetPostId 게시글을 신고한 이력 조회
    Optional<Blame> findByBlameMemberIdAndTargetPostId(Long blameMemberId, Long targetCommentId);

    //blameMember 회원이 targetCommentId 댓글을 신고했는지 여부
    boolean existsByBlameMemberIdAndTargetCommentId(Long blameMemberId, Long targetCommentId);

    //blameMember 회원이 targetCommentId 댓글을 신고한 이력 조회
    Optional<Blame> findByBlameMemberIdAndTargetCommentId(Long blameMemberId, Long targetCommentId);

    //blameMember 회원의 신고한 이력 조회
    List<Blame> findByBlameMemberId(Long blameMemberId);
}

