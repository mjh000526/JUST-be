package com.example.just.Service;

import com.example.just.BasicResponse;
import com.example.just.Dao.Member;
import com.example.just.Dao.Role;
import com.example.just.Dto.MemberDto;
import com.example.just.Dto.TokenDto;
import com.example.just.Mapper.MemberMapper;
import com.example.just.Repository.MemberRepository;
import com.example.just.jwt.JwtFilter;
import com.example.just.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberMapper memberMapper;
    @Autowired
    JwtProvider jwtProvider;

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

    public ResponseEntity changeRefresh(HttpServletRequest request){
        String token = jwtProvider.getRefreshToken(request);
        String accesstoken = null;
        String refreshtoken = null;
        if(token == null) return new ResponseEntity<>("리프레시 토큰이 없습니다.",HttpStatus.OK);
        else {
            Member member = memberRepository.findByRefreshToken(token).get();
            if(member == null) return new ResponseEntity<>("해당 멤버가 없습니다.",HttpStatus.OK);
            Member newMember = Member.builder()
                    .email(member.getEmail())
                    .provider(member.getProvider())
                    .provider_id(member.getProvider_id())
                    .authority(Role.ROLE_USER)
                    .nickname(member.getNickname())
                    .blameCount(0)
                    .blamedCount(0)
                    .refreshToken(null)
                    .build();
            memberRepository.save(newMember);
            accesstoken = jwtProvider.createaccessToken(newMember);
            refreshtoken = jwtProvider.createRefreshToken(newMember);
            newMember.setRefreshToken(refreshtoken);
            memberRepository.save(newMember);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accesstoken);
            httpHeaders.add("refresh_token",refreshtoken);
        }
        return new ResponseEntity<>(new TokenDto(accesstoken,refreshtoken), HttpStatus.OK);
    }
    public ResponseEntity drop(HttpServletRequest request){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출

        String email = jwtProvider.getEmailFromToken(token);
        if(memberRepository.existsById(id)){
            memberRepository.deleteById(id);
            return new ResponseEntity<>(email + "삭제",HttpStatus.OK);
        }
        return new ResponseEntity<>("해당 회원이 존재하지 않습니다.",HttpStatus.BAD_REQUEST);
    }

    public List<MemberDto> findMembers() {
        List<Member> Memberlist = memberRepository.findAll();
        List<MemberDto> memberDtoList =new ArrayList<>();
        for(Member member :Memberlist){
            MemberDto memberDto = memberMapper.toDto(member);
            memberDtoList.add(memberDto);
        }
        return memberDtoList;
    }
}
