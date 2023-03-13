package com.example.just.Service;

import com.example.just.BasicResponse;
import com.example.just.Dao.Member;
import com.example.just.Dto.MemberDto;
import com.example.just.Mapper.MemberMapper;
import com.example.just.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberMapper memberMapper;

    public ResponseEntity<BasicResponse> join(MemberDto memberDto) {
        Member member = memberMapper.toEntity(memberDto);

        memberRepository.save(member);

        BasicResponse basicResponse = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("회원 정보 저장 성공!")
                .result(null)
                .count(1).build();
        return new ResponseEntity<>(basicResponse, basicResponse.getHttpStatus());
    }
}
