package com.example.just.Service;

import com.example.just.Dao.Blame;
import com.example.just.Document.PostDocument;
import com.example.just.Repository.BlameRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;
import com.example.just.jwt.JwtProvider;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    @Autowired
    PostContentESRespository postContentESRespository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    BlameRepository blameRepository;

    @Autowired
    MemberRepository memberRepository;

    public ResponseEntity searchPostContent(HttpServletRequest request,String keyword){
        String token = jwtProvider.getAccessToken(request);
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        List<Blame> blames = blameRepository.findByBlameMemberId(id);
        //유저가 신고한 게시글 id들
        List<Long> postIds = blames.stream()
                .map(Blame::getTargetPostId)
                .collect(Collectors.toList());
        //유저가 신고한 회원 id들
        List<Long> memberIds = blames.stream()
                .map(Blame::getTargetMemberId)
                .collect(Collectors.toList());

        List<PostDocument> searchList = postContentESRespository.findByPostContent_ContentContains(keyword);

        List<PostDocument> filterList = searchList.stream()
                .filter(postDocument -> !postIds.contains(postDocument.getId()))
                .filter(postDocument -> !memberIds.contains(postDocument.getMemberId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(filterList);
    }

}
