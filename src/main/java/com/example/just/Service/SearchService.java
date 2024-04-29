package com.example.just.Service;

import com.example.just.Dao.Blame;
import com.example.just.Document.HashTagDocument;
import com.example.just.Document.PostDocument;
import com.example.just.Repository.BlameRepository;
import com.example.just.Repository.HashTagESRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostContentESRespository;
import com.example.just.Repository.PostRepository;
import com.example.just.Response.ResponseMessage;
import com.example.just.Response.ResponseSearchDto;
import com.example.just.jwt.JwtProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SearchService { //검색(ELK) 관련 서비스

    @Autowired
    PostContentESRespository postContentESRespository;

    @Autowired
    HashTagESRepository hashTagESRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    BlameRepository blameRepository;

    @Autowired
    MemberRepository memberRepository;


    //게시글 내용 검색
    public ResponseEntity searchPostContent(HttpServletRequest request,String keyword,int page){
        //헤더로부터 토큰 추출
        String token = jwtProvider.getAccessToken(request);
        //토큰이 없을 경우 검색 기능 사용 불가
        if(token == null){
            return new ResponseEntity(new ResponseMessage("로그인 후 검색가능합니다."),null, HttpStatus.BAD_REQUEST);
        }
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰으로 id값 추출
        //클라이언트의 신고 이력 조회
        List<Blame> blames = blameRepository.findByBlameMemberId(id);
        //유저가 신고한 게시글 id들
        List<Long> postIds = blames.stream()
                .map(Blame::getTargetPostId)
                .collect(Collectors.toList());
        //유저가 신고한 회원 id들
        List<Long> memberIds = blames.stream()
                .map(Blame::getTargetMemberId)
                .collect(Collectors.toList());

        //검색한 키워드를 내용에 포함하는 게시글 리스트 조회
        List<PostDocument> searchList = postContentESRespository.findByPostContentContaining(keyword);
        //검색한 키워드를 포함하는 게시글이 없을 경우
        if(searchList.isEmpty()){
            return new ResponseEntity(new ResponseMessage("해당 내용을 포함하는 게시글이 존재하지 않습니다."), null, HttpStatus.BAD_REQUEST);
        }

        //조회된 게시글 리스트에서 클라이언트가 신고한 게시글이나 회원에 대한 데이터 삭제
        List<PostDocument> filterList = searchList.stream()
                .filter(postDocument -> !postIds.contains(postDocument.getId()))
                .filter(postDocument -> !memberIds.contains(postDocument.getMemberId()))
                .collect(Collectors.toList());

        //필터링된 데이터들을 ResponseSearchDto 포맷에 맞게 변환
        List<ResponseSearchDto> result = filterList.stream()
                .map(postDocument -> new ResponseSearchDto(postDocument,id))
                .collect(Collectors.toList());

        //변환된 응답 데이터들을 최신순으로 페이지네이션
        PageRequest pageRequest = PageRequest.of(page,10);
        result.sort(Comparator.comparing(ResponseSearchDto::getPost_create_time).reversed());
        int start = (int) pageRequest.getOffset();
        if (start >= result.size()) {
            return new ResponseEntity(new ResponseMessage("페이지를 초과하엿습니다."),null,HttpStatus.BAD_REQUEST);
        }
        int end = Math.min((start + pageRequest.getPageSize()),result.size());
        Page<ResponseSearchDto> postPage = new PageImpl<>(result.subList(start,end), pageRequest, result.size());
        return ResponseEntity.ok(postPage);
    }

    //태그 자동완성
    public ResponseEntity getAutoTag(String str,int page){
        List<HashTagDocument> hashTagDocuments = new ArrayList<HashTagDocument>();
        //빈값으로 태그를 검색할 경우 태그가 사용된 횟수가 많은 순으로 조회
        if(str.equals("") || str.equals(null)){
            hashTagDocuments = hashTagESRepository.findAll(Sort.by(Direction.DESC,"tagCount"));
        }else {
            //빈값이 아니면 str을 문자열에 포함하는 태그들을 사용된 횟수가 많은 순으로 조회
            hashTagDocuments = hashTagESRepository.findByNameContaining(str,Sort.by(Direction.DESC,"tagCount"));
        }
        //태그가 존재하지 않을 경우
        if(hashTagDocuments.isEmpty()) {
            return new ResponseEntity(new ResponseMessage("태그가 존재하지 않습니다."), null, HttpStatus.BAD_REQUEST);
        }
        //조회된 태그들 페이지네이션
        PageRequest pageRequest = PageRequest.of(page,10);
        int start = (int) pageRequest.getOffset();
        if (start >= hashTagDocuments.size()) {
            return new ResponseEntity(new ResponseMessage("페이지를 초과하엿습니다."),null,HttpStatus.BAD_REQUEST);
        }
        int end = Math.min((start + pageRequest.getPageSize()),hashTagDocuments.size());
        Page<HashTagDocument> postPage = new PageImpl<>(hashTagDocuments.subList(start,end), pageRequest, hashTagDocuments.size());
        return ResponseEntity.ok(postPage);
    }

    //태그로 게시글 조회
    public ResponseEntity searchTagPost(HttpServletRequest request,String tag,int page){
        //헤더에서 토큰 추출
        String token = jwtProvider.getAccessToken(request);
        //토큰이 없을 경우 검색 기능 사용 불가
        if(token == null){
            return new ResponseEntity(new ResponseMessage("로그인 후 검색가능합니다."),null, HttpStatus.BAD_REQUEST);
        }
        //토큰으로부터 클라이언트 id 추출
        Long id = Long.valueOf(jwtProvider.getIdFromToken(token));
        //사용자의 신고 이력 조회
        List<Blame> blames = blameRepository.findByBlameMemberId(id);
        //유저가 신고한 게시글 id들
        List<Long> postIds = blames.stream()
                .map(Blame::getTargetPostId)
                .collect(Collectors.toList());
        //유저가 신고한 회원 id들
        List<Long> memberIds = blames.stream()
                .map(Blame::getTargetMemberId)
                .collect(Collectors.toList());

        //tag값을 가진 게시글 전체 조회
        List<PostDocument> searchList = postContentESRespository.findByHashTagIn(tag);
        //tag값을 가진 게시글이 존재하지 않을 경우
        if(searchList.isEmpty()){
            return new ResponseEntity(new ResponseMessage("해당 태그을 가진 게시글이 존재하지 않습니다."), null, HttpStatus.BAD_REQUEST);
        }
        //조회된 게시글 리스트에서 사용자가 신고한 데이터들을 제외
        List<PostDocument> filterList = searchList.stream()
                .filter(postDocument -> !postIds.contains(postDocument.getId()))
                .filter(postDocument -> !memberIds.contains(postDocument.getMemberId()))
                .collect(Collectors.toList());

        //필터링된 데이터들을 ResponseSearchDto형식으로 변환
        List<ResponseSearchDto> result = filterList.stream()
                .map(postDocument -> new ResponseSearchDto(postDocument,id))
                .collect(Collectors.toList());

        //변환된 데이터들을 페이지네이션
        PageRequest pageRequest = PageRequest.of(page,10);
        result.sort(Comparator.comparing(ResponseSearchDto::getPost_create_time).reversed());//최신순 조회
        int start = (int) pageRequest.getOffset();
        if (start >= result.size()) {
            return new ResponseEntity(new ResponseMessage("페이지를 초과하엿습니다."),null,HttpStatus.BAD_REQUEST);
        }
        int end = Math.min((start + pageRequest.getPageSize()),result.size());
        Page<ResponseSearchDto> postPage = new PageImpl<>(result.subList(start,end), pageRequest, result.size());
        return ResponseEntity.ok(postPage);
    }

}
