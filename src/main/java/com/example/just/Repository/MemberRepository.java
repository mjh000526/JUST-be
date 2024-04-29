package com.example.just.Repository;

import com.example.just.Dao.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    //이메일에 해당하는 회원 정보 조회
    Member findByEmail(String Email);

    //int값 이상의(일정 수치이상의 신고한횟수) member list 내림차순으로 추출
    List<Member> findByBlameCountGreaterThanEqualOrderByBlameCountDesc(int blame_count);

    //int값 이상의(일정 수치이상의 신고받은횟수) member list 내림차순으로 추출
    List<Member> findByBlamedCountGreaterThanEqualOrderByBlamedCountDesc(int blamed_count);

    //해당 재발급 토큰에 대한 존재 여부
    boolean existsByRefreshToken(String token);

    //해당 재발급 토큰을 가진 회원 객체 조회
    Optional<Member> findByRefreshToken(String token);
}
