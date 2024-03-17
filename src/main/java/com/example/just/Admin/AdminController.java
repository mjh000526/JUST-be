package com.example.just.Admin;

import com.example.just.Dao.Comment;
import com.example.just.Dao.HashTag;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
<<<<<<< HEAD
import com.example.just.Repository.HashTagMapRepository;
import com.example.just.Response.ResponseUpdateHashtagsDto;
import com.example.just.Repository.CommentRepository;
import com.example.just.Repository.HashTagRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
import com.example.just.Service.ResponsePost;
=======
import com.example.just.Repository.*;
import com.example.just.Response.ResponseUpdateHashtagsDto;
import com.example.just.Service.*;
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD
import com.example.just.Service.MemberService;
import com.example.just.Service.CommentService;
import com.example.just.Service.PostService;
=======

import java.util.Collections;
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PostService postService;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MemberRepository memberRepository;
<<<<<<< HEAD


    @Autowired
    private PostRepository postRepository;
=======
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    @Autowired
    private HashTagRepository hashTagRepository;
    @Autowired
    private HashTagMapRepository hashTagMapRepository;
<<<<<<< HEAD

    @ApiOperation(value = "멤버 리스트 불러옴 admin페이지에서 members 페이지로 이동할때")
    @Operation(summary = "members리스트 뽑는다", description = "\n admin페이지->members페이지-> 리스트 리턴")
=======
    @Autowired
    private HashTagESRepository hashTagESRepository;
    @Autowired
    private BlameService blameService;

    @ApiOperation(value = "멤버 리스트 불러옴 admin 페이지에서 members 페이지로 이동할때")
    @Operation(summary = "members 리스트 뽑는다", description = "\n admin 페이지->members 페이지-> 리스트 리턴")
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    @GetMapping("/members")
    public ResponseEntity<List<Member>> memberList() {
        List<Member> members = memberService.findMembers();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }


<<<<<<< HEAD
=======
    @ApiOperation(value = "신고받은 멤버 조회")
    @Operation(summary = "신고받은 멤버 조회", description = "\n 신고받은 횟수가 0보다 큰 멤버 조회")
    @GetMapping("/reported-members")
    public ResponseEntity<List<Member>> getReportedMembers() {
        // 1 이상의 신고를 받은 멤버만 조회
        ResponseEntity<List<Member>> reportedMembersResponse = blameService.getBlamedList("member");
        List<Member> reportedMembers = reportedMembersResponse.getBody();
        return new ResponseEntity<>(reportedMembers, HttpStatus.OK);
    }


>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    @ApiOperation(value = "멤버페이지에서 회원 삭제 기능 ")
    @Operation(summary = "회원 삭제", description = "\n member_id 헤더로 받고 데이터베이스 비교 후 회원 삭제")
    @DeleteMapping("/members/{member_id}")
    public ResponseEntity drop(@PathVariable Long member_id) {
        if (memberRepository.existsById(member_id)) {
            memberRepository.deleteById(member_id);
            return new ResponseEntity<>("회원 삭제 성공", HttpStatus.OK);
        }
        return new ResponseEntity<>("해당 회원이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
    }

<<<<<<< HEAD
    @ApiOperation(value = "comments페이지에서 comment 리스트 불러옴")
    @Operation(summary = "comments리스트", description = "\n admin페이지 -> comments->페이지로 return하여 리스트 뽑")
=======
    @ApiOperation(value = "comments 페이지에서 comment 리스트 불러옴")
    @Operation(summary = "comments 리스트", description = "\n admin 페이지 -> comments->페이지로 return 하여 리스트 뽑")
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> commentList() {
        List<Comment> comments = commentService.getAllComments();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    //댓글 삭제
    @ApiOperation(value = "댓글 아이디와 멤버 아이디 비교 후 삭제 ")
<<<<<<< HEAD
    @Operation(summary = "댓글 삭제", description = "\n comment_id, member_id 헤더로 받고 데이터베이스 비교 후 회원 삭제")
    @DeleteMapping("/comments/{comment_id}/{member_id}")
=======
    @Operation(summary = "댓글 삭제", description = "\n comment_id, 헤더로 받고 데이터베이스 비교 후 회원 삭제")
    @DeleteMapping("/comments/{comment_id}/")
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
    public ResponseEntity<String> deleteComment(@PathVariable Long comment_id) {
        Comment comment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new RuntimeException("부모 댓글이 존재하지 않습니다."));
        List<Comment> children = comment.getChildren();
        if (children != null) {
<<<<<<< HEAD
            for (Comment child : children) {
                commentRepository.delete(child);
            }
=======
            commentRepository.deleteAll(children);
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
        }
        commentRepository.deleteById(comment_id);
        return ResponseEntity.ok("ok");
    }


<<<<<<< HEAD
    @ApiOperation(value = "게시물 리스트 불러옴 posts페이지에서")
    @Operation(summary = "게시글 리스트", description = "\n admin 페이지-> posts페이지-> return하여 post list 출력")
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> postList() {
=======
    @ApiOperation(value = "게시물 리스트 불러옴 posts 페이지에서")
    @Operation(summary = "게시글 리스트", description = "\n admin 페이지-> posts 페이지-> return 하여 post list 출력")
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> PostList() {
>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
        List<Post> posts = postService.getAllPostList();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }


    //게시글 삭제
    @ApiOperation(value = "포스트페이지에서 포스트 삭제 ")
    @Operation(summary = "게시글 삭제", description = "\n post_id 헤더로 받고 데이터베이스 비교 후 회원 삭제")
    @DeleteMapping("/posts/{post_id}")
    public ResponsePost deletePost(@PathVariable Long post_id) throws NotFoundException {
        postService.deletePost(post_id);
        ResponsePost responsePost = new ResponsePost(post_id, "삭제 완료");
        return responsePost;
    }
<<<<<<< HEAD
/*
    @ApiOperation(value = "포스트페이지 해시태그 수정 ")
    @Operation(summary = "해시태그 수정", description = "\n hashtag_id 헤더로 받고 내용을 수정하면 새로운 해시태그를 생성하여 저장 ex) id_101 -> id_104로")
    @PutMapping("/posts/{hashtag_id}")
    public ResponseEntity<?> updateHashtags(@PathVariable Long hashtag_id, @RequestBody List<String> newHashtags)
            throws NotFoundException {
        Optional<HashTag> optionalHashtag = hashTagRepository.findById(hashtag_id);

        if (!optionalHashtag.isPresent()) {
            throw new NotFoundException();
        }

        HashTag existingHashtag = optionalHashtag.get();
        Post post = existingHashtag.getPost();

        // 기존 게시물의 해시태그 제거
        List<HashTag> existingHashtags = hashTagRepository.findByPost(post);
        for (HashTag hashtag : existingHashtags) {
            hashTagRepository.deleteById(hashtag.getId());
        }

        // 새로운 해시태그를 생성 저장
        List<HashTag> updatedHashtags = newHashtags.stream()
                .map(name -> {
                    HashTag hashtag = new HashTag();
                    hashtag.setName(name);
                    hashtag.setPost(post);
                    return hashtag;
                })
                .collect(Collectors.toList());

        hashTagRepository.saveAll(updatedHashtags);

        // 업데이트된 정보 DTO 반환
        ResponseUpdateHashtagsDto response = new ResponseUpdateHashtagsDto(hashtag_id, newHashtags);
        return ResponseEntity.ok(response);
    }

*/
}


=======

    @ApiOperation(value = "포스트페이지 해시태그 수정 ")
    @Operation(summary = "해시태그 수정", description = "\n hashtag_id 헤더로 받고 내용을 수정하면 새로운 해시태그를 생성하여 저장 ex) id_101 -> id_104로")
    @PutMapping("/hashtags/{hashtag_id}")
    public ResponseEntity<?> updateHashTag(@PathVariable Long hashtag_id, @RequestBody String newName) {
        Optional<HashTag> optionalHashTag = hashTagRepository.findById(hashtag_id);

        if (!optionalHashTag.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        HashTag hashTag = optionalHashTag.get();
        hashTag.setName(newName);
        hashTagRepository.save(hashTag);

        return ResponseEntity.ok("해시태그 내용이 성공적으로 수정되었습니다.");
    }
    /*

     */
}





>>>>>>> aea347125278b8318ff91f76045a9a2d7fb0c828
