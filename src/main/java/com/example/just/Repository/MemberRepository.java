package com.example.just.Repository;

import com.example.just.Dao.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByEmail(String Email);
    //int값 이상의(일정 수치이상의 신고한횟수) member list 내림차순으로 추출
    List<Member> findByBlameCountGreaterThanEqualOrderByBlameCountDesc(int blame_count);

    //int값 이상의(일정 수치이상의 신고받은횟수) member list 내림차순으로 추출
    List<Member> findByBlamedCountGreaterThanEqualOrderByBlamedCountDesc(int blamed_count);

    boolean existsByRefreshToken(String token);
    Optional<Member> findByRefreshToken(String token);
}
