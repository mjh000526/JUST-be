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
public class  BlameService {
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

    //게시글 및 댓글 신고(type값은 "post"또는"comment"로 할 예정
    public ResponseEntity writeMemberBlame(HttpServletRequest request, Long target_id,Long type){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
        Member member = memberRepository.findById(id).get();
        member.addBlame();
        memberRepository.save(member);
        if(!memberRepository.findById(target_id).isPresent()) {
            return new ResponseEntity<>(new ResponseBlameDto(null,"신고할 회원이 존재하지 않습니다."),HttpStatus.NOT_FOUND);
        }
        else if(blameRepository.existsByBlameMemberIdAndTargetMemberId(id,target_id)){
            return new ResponseEntity<>(new ResponseBlameDto(null,"이미 신고한 회원입니다."),HttpStatus.NOT_FOUND);
        }
        member = memberRepository.findById(target_id).get();
        member.addBlamed();
        memberRepository.save(member);
        Blame blame = Blame.builder()
                .blameMemberId(id)
                .targetMemberId(target_id)
                .targetIndex(type)
                .targetPostId(-1L)
                .targetCommentId(-1L)
                .build();
        blame = blameRepository.save(blame);
        return new ResponseEntity<>(new ResponseBlameDto(blame,"회원 신고완료"), HttpStatus.OK);
    }

    public ResponseEntity writePostBlame(HttpServletRequest request, Long target_id,Long type){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
        Member member = memberRepository.findById(id).get();
        member.addBlame();
        memberRepository.save(member);
        if(!postRepository.findById(target_id).isPresent()) {
            return new ResponseEntity<>(new ResponseBlameDto(null,"신고할 게시글이 존재하지 않습니다."),HttpStatus.NOT_FOUND);
        }
        else if(blameRepository.existsByBlameMemberIdAndTargetPostId(id,target_id)){
            return new ResponseEntity<>(new ResponseBlameDto(null,"이미 신고한 게시글입니다."),HttpStatus.NOT_FOUND);
        }
        Blame blame = Blame.builder()
                .blameMemberId(id)
                .targetMemberId(-1L)
                .targetIndex(type)
                .targetPostId(target_id)
                .targetCommentId(-1L)
                .build();
        blame = blameRepository.save(blame);
        return new ResponseEntity<>(new ResponseBlameDto(blame,"게시글 신고완료"), HttpStatus.OK);
    }

    public ResponseEntity writeCommentBlame(HttpServletRequest request, Long target_id,Long type){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
        Member member = memberRepository.findById(id).get();
        member.addBlame();
        memberRepository.save(member);
        if(!commentRepository.findById(target_id).isPresent()) {
            return new ResponseEntity<>(new ResponseBlameDto(null,"신고할 댓글이 존재하지 않습니다."),HttpStatus.NOT_FOUND);
        }
        else if(blameRepository.existsByBlameMemberIdAndTargetCommentId(id,target_id)){
            return new ResponseEntity<>(new ResponseBlameDto(null,"이미 신고한 댓글입니다."),HttpStatus.NOT_FOUND);
        }
        Blame blame = Blame.builder()
                .blameMemberId(id)
                .targetMemberId(-1L)
                .targetIndex(type)
                .targetPostId(-1L)
                .targetCommentId(target_id)
                .build();
        blame = blameRepository.save(blame);
        return new ResponseEntity<>(new ResponseBlameDto(blame,"댓글 신고완료"), HttpStatus.OK);
    }

    public ResponseEntity deleteMemberBlame(HttpServletRequest request, Long target_id){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
        Member member = memberRepository.findById(id).get();
        member.removeBlame();
        memberRepository.save(member);
        if(!memberRepository.findById(target_id).isPresent()) {
            return new ResponseEntity<>(new ResponseBlameDto(null,"회원이 존재하지 않습니다."),HttpStatus.NOT_FOUND);
        }
        else if(!blameRepository.existsByBlameMemberIdAndTargetMemberId(id,target_id)){
            return new ResponseEntity<>(new ResponseBlameDto(null,"신고하지 않은 회원입니다."),HttpStatus.NOT_FOUND);
        }
        Blame blame = blameRepository.findByBlameMemberIdAndTargetMemberId(id,target_id).get();
        blameRepository.delete(blame);
        return new ResponseEntity<>(new ResponseBlameDto(null,"회원 신고취소"), HttpStatus.OK);
    }

    public ResponseEntity deletePostBlame(HttpServletRequest request, Long target_id){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
        Member member = memberRepository.findById(id).get();
        member.addBlame();
        memberRepository.save(member);
        if(!postRepository.findById(target_id).isPresent()) {
            return new ResponseEntity<>(new ResponseBlameDto(null,"게시글이 존재하지 않습니다."),HttpStatus.NOT_FOUND);
        }
        else if(!blameRepository.existsByBlameMemberIdAndTargetPostId(id,target_id)){
            return new ResponseEntity<>(new ResponseBlameDto(null,"신고하지 않은 게시글입니다."),HttpStatus.NOT_FOUND);
        }
        Blame blame = blameRepository.findByBlameMemberIdAndTargetPostId(id,target_id).get();
        blameRepository.delete(blame);
        return new ResponseEntity<>(new ResponseBlameDto(null,"게시글 신고취소"), HttpStatus.OK);
    }

    public ResponseEntity deleteCommentBlame(HttpServletRequest request, Long target_id){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
        Member member = memberRepository.findById(id).get();
        member.addBlame();
        memberRepository.save(member);
        if(!commentRepository.findById(target_id).isPresent()) {
            return new ResponseEntity<>(new ResponseBlameDto(null,"댓글이 존재하지 않습니다."),HttpStatus.NOT_FOUND);
        }
        else if(!blameRepository.existsByBlameMemberIdAndTargetCommentId(id,target_id)){
            return new ResponseEntity<>(new ResponseBlameDto(null,"신고하지 않은 댓글입니다."),HttpStatus.NOT_FOUND);
        }
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
