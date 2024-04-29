package com.example.just.Service;

import com.example.just.Dao.Blame;
import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Repository.BlameRepository;
import com.example.just.Repository.CommentRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
import com.example.just.Response.ResponseBlameDto;
import com.example.just.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class  BlameService { //신고 관련 기능 서비스
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BlameRepository blameRepository;
    @Autowired
    private JwtProvider jwtProvider;

    //회원 신고
    public ResponseEntity writeMemberBlame(HttpServletRequest request, Long target_id,Long type){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
        Member member = memberRepository.findById(id).get();//헤더에 있는 토큰으로 클라이언트 식별
        member.addBlame();//신고횟수 증감
        memberRepository.save(member);//신고 처리 정보 저장
        //신고받은 회원의 id가 존재하지 않을 경우
        if(!memberRepository.findById(target_id).isPresent()) {
            return new ResponseEntity<>(new ResponseBlameDto(null,"신고할 회원이 존재하지 않습니다."),HttpStatus.NOT_FOUND);
        }
        //클라이언트가 신고받은 회원을 이미 신고한 전적이 있는 경우
        else if(blameRepository.existsByBlameMemberIdAndTargetMemberId(id,target_id)){
            return new ResponseEntity<>(new ResponseBlameDto(null,"이미 신고한 회원입니다."),HttpStatus.NOT_FOUND);
        }
        //신고 받은 사람의 정보 조회
        member = memberRepository.findById(target_id).get();
        //신고받은횟수 증감
        member.addBlamed();
        memberRepository.save(member);//해당 정보 저장
        //신고 객체 생성
        Blame blame = Blame.builder()
                .blameMemberId(id)
                .targetMemberId(target_id)
                .targetIndex(type)
                .targetPostId(-1L) //회원 신고이기 때문에 게시글과 댓글 -1
                .targetCommentId(-1L)
                .build();
        blame = blameRepository.save(blame);//객체 저장
        return new ResponseEntity<>(new ResponseBlameDto(blame,"회원 신고완료"), HttpStatus.OK);
    }

    //게시글 신고
    public ResponseEntity writePostBlame(HttpServletRequest request, Long target_id,Long type){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
        Member member = memberRepository.findById(id).get();
        member.addBlame();
        memberRepository.save(member);
        //타겟 게시글의 정보가 DB에 존재하지 않을 경우
        if(!postRepository.findById(target_id).isPresent()) {
            return new ResponseEntity<>(new ResponseBlameDto(null,"신고할 게시글이 존재하지 않습니다."),HttpStatus.NOT_FOUND);
        }
        //해당 회원이 타겟 게시글을 신고한 전적이 있는 경우
        else if(blameRepository.existsByBlameMemberIdAndTargetPostId(id,target_id)){
            return new ResponseEntity<>(new ResponseBlameDto(null,"이미 신고한 게시글입니다."),HttpStatus.NOT_FOUND);
        }
        //신고 객체 생성
        Blame blame = Blame.builder()
                .blameMemberId(id)
                .targetMemberId(-1L)//게시글 신고이기 때문에 회원id와 댓글 id에 -1
                .targetIndex(type)
                .targetPostId(target_id)
                .targetCommentId(-1L)
                .build();
        blame = blameRepository.save(blame);//객체 저장
        return new ResponseEntity<>(new ResponseBlameDto(blame,"게시글 신고완료"), HttpStatus.OK);
    }

    //댓글 신고
    public ResponseEntity writeCommentBlame(HttpServletRequest request, Long target_id,Long type){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
        Member member = memberRepository.findById(id).get();
        member.addBlame();
        memberRepository.save(member);
        //타겟 댓글의 정보가 DB에 존재하지 않을 경우
        if(!commentRepository.findById(target_id).isPresent()) {
            return new ResponseEntity<>(new ResponseBlameDto(null,"신고할 댓글이 존재하지 않습니다."),HttpStatus.NOT_FOUND);
        }
        //클라이언트가 타겟 댓글을 이미 신고한 전적이 있는 경우
        else if(blameRepository.existsByBlameMemberIdAndTargetCommentId(id,target_id)){
            return new ResponseEntity<>(new ResponseBlameDto(null,"이미 신고한 댓글입니다."),HttpStatus.NOT_FOUND);
        }
        //신고 객체 생성
        Blame blame = Blame.builder()
                .blameMemberId(id)
                .targetMemberId(-1L)//댓글 신고이기 때문에 회원id와 게시글 id에 -1
                .targetIndex(type)
                .targetPostId(-1L)
                .targetCommentId(target_id)
                .build();
        blame = blameRepository.save(blame);//객체 저장
        return new ResponseEntity<>(new ResponseBlameDto(blame,"댓글 신고완료"), HttpStatus.OK);
    }

    //회원 신고 취소
    public ResponseEntity deleteMemberBlame(HttpServletRequest request, Long target_id){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
        Member member = memberRepository.findById(id).get();
        member.removeBlame();
        memberRepository.save(member);
        //타겟 회원이 존재하지 않을 경우
        if(!memberRepository.findById(target_id).isPresent()) {
            return new ResponseEntity<>(new ResponseBlameDto(null,"회원이 존재하지 않습니다."),HttpStatus.NOT_FOUND);
        }
        //타겟 회원에 대한 신고 전적이 없을 경우
        else if(!blameRepository.existsByBlameMemberIdAndTargetMemberId(id,target_id)){
            return new ResponseEntity<>(new ResponseBlameDto(null,"신고하지 않은 회원입니다."),HttpStatus.NOT_FOUND);
        }
        //타겟 회원 신고 이력 조회 및 삭제
        Blame blame = blameRepository.findByBlameMemberIdAndTargetMemberId(id,target_id).get();
        blameRepository.delete(blame);
        return new ResponseEntity<>(new ResponseBlameDto(null,"회원 신고취소"), HttpStatus.OK);
    }

    //게시글 신고 취소
    public ResponseEntity deletePostBlame(HttpServletRequest request, Long target_id){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
        Member member = memberRepository.findById(id).get();
        member.addBlame();
        memberRepository.save(member);
        //타겟 게시글의 정보가 DB에 존재하지 않을 경우
        if(!postRepository.findById(target_id).isPresent()) {
            return new ResponseEntity<>(new ResponseBlameDto(null,"게시글이 존재하지 않습니다."),HttpStatus.NOT_FOUND);
        }
        //타겟 게시글에 대한 신고 전적이 없을 경우
        else if(!blameRepository.existsByBlameMemberIdAndTargetPostId(id,target_id)){
            return new ResponseEntity<>(new ResponseBlameDto(null,"신고하지 않은 게시글입니다."),HttpStatus.NOT_FOUND);
        }
        //타겟 게시글에 대한 신고 전적 조회 및 삭제
        Blame blame = blameRepository.findByBlameMemberIdAndTargetPostId(id,target_id).get();
        blameRepository.delete(blame);
        return new ResponseEntity<>(new ResponseBlameDto(null,"게시글 신고취소"), HttpStatus.OK);
    }

    //댓글 신고 취소
    public ResponseEntity deleteCommentBlame(HttpServletRequest request, Long target_id){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
        Member member = memberRepository.findById(id).get();
        member.addBlame();
        memberRepository.save(member);
        //타겟 댓글에 대한 정보가 DB에 존재하지 않을 경우
        if(!commentRepository.findById(target_id).isPresent()) {
            return new ResponseEntity<>(new ResponseBlameDto(null,"댓글이 존재하지 않습니다."),HttpStatus.NOT_FOUND);
        }
        //타겟 댓글에 대한 신고 전적이 없을 경우
        else if(!blameRepository.existsByBlameMemberIdAndTargetCommentId(id,target_id)){
            return new ResponseEntity<>(new ResponseBlameDto(null,"신고하지 않은 댓글입니다."),HttpStatus.NOT_FOUND);
        }
        //타겟 댓글에 대한 신고 전적 조회 및 삭제
        Blame blame = blameRepository.findByBlameMemberIdAndTargetCommentId(id,target_id).get();
        blameRepository.delete(blame);
        return new ResponseEntity<>(new ResponseBlameDto(null,"댓글 신고취소"), HttpStatus.OK);
    }
    //신고당한 상위 10개의 리스트가져오기(type값으로 "member","comment","post"에 따라 값달라짐)
    public ResponseEntity getBlamedList(String type){
        List<?> list = new ArrayList<>();
        if(type.equals("member")){
            list = memberRepository.findByBlamedCountGreaterThanEqualOrderByBlamedCountDesc(1); //변수 이상의 신고를 받은 객체반환
        }
        else if(type.equals("post")){
            list = postRepository.findByBlamedCountGreaterThanEqualOrderByBlamedCountDesc(1);
        }
        else if(type.equals("comment")){
            list = commentRepository.findByBlamedCountGreaterThanEqualOrderByBlamedCountDesc(1);
        }
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    //신고한 회수가 많은 유저 조회
    public ResponseEntity getBlameList(){
        List<Member> list = memberRepository.findByBlameCountGreaterThanEqualOrderByBlameCountDesc(1);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }
}
