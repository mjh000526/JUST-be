package com.example.just.Service;

import com.example.just.Dao.Blame;
import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Repository.BlameRepository;
import com.example.just.Repository.CommentRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
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
    public ResponseEntity writeBlame(HttpServletRequest request, Long target_id, String type){
        String token = request.getHeader("access_token");
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id추출
//        if(blameRepository.findByBlameMemberIdAndTargetIdAndTargetType(id,target_id,type)!=null){
//            return new ResponseEntity<>("이미 접수된 신고입니다.", HttpStatus.OK);
//        }
        Member member = memberRepository.findById(id).get();
        member.addBlame();
        memberRepository.save(member);
        if(type.equals("post")){
            Post post = postRepository.findById(target_id).get();
            post.addBlamed();
            postRepository.save(post);
            member = post.getMember();
            member.addBlamed();
        }
        else if(type.equals("comment")){
            Comment comment = commentRepository.findById(target_id).get();
            comment.addBlamed();
            commentRepository.save(comment);
            member = comment.getMember();
            member.addBlamed();
        }
        memberRepository.save(member);
        Blame blame = Blame.builder()
                .blameMemberId(id)
                .targetMemberId(member.getId())
                .blameDatetime(new Date(System.currentTimeMillis()))
                .build();
        blameRepository.save(blame);
        return new ResponseEntity<>(blame, HttpStatus.OK);
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
