package com.example.just.Service;


import com.example.just.BasicResponse;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dto.PostDto;
import com.example.just.Mapper.PostMapper;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;



@Service
public class PostService {


    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    public ResponseEntity<BasicResponse> write(Long member_id, PostDto postDto) {


        Member member = memberRepository.findById(member_id).orElseGet(Member::new);
        Post post = new Post(postDto.getPost_content(),
                postDto.getPost_tag(), postDto.getPost_like(),
                postDto.getPost_create_time(), postDto.isSecret(),
                postDto.getEmoticon(), postDto.getPost_category(),
                member);

        postRepository.save(post);

        BasicResponse basicResponse = BasicResponse.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("글 저장 성공!")
                .result(null)
                .count(1).build();


        return new ResponseEntity<>(basicResponse, basicResponse.getHttpStatus());
    }
}
