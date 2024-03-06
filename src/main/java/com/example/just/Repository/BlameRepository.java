package com.example.just.Repository;

import com.example.just.Dao.Blame;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlameRepository extends JpaRepository<Blame, Long> {
    boolean existsByBlameMemberIdAndTargetMemberId(Long blameMemberId,Long targetMemberId);
    Optional<Blame> findByBlameMemberIdAndTargetMemberId(Long blameMemberId, Long targetCommentId);
    boolean existsByBlameMemberIdAndTargetPostId(Long blameMemberId, Long targetPostId);
    Optional<Blame> findByBlameMemberIdAndTargetPostId(Long blameMemberId, Long targetCommentId);
    boolean existsByBlameMemberIdAndTargetCommentId(Long blameMemberId, Long targetCommentId);
    Optional<Blame> findByBlameMemberIdAndTargetCommentId(Long blameMemberId, Long targetCommentId);
    List<Blame> findByBlameMemberId(Long blameMemberId);
}

