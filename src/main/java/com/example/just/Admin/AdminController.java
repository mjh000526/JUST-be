package com.example.just.Admin;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Repository.CommentRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
import com.example.just.Service.ResponsePost;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.just.Service.MemberService;
import com.example.just.Service.CommentService;
import com.example.just.Service.PostService;
import java.util.List;
import org.springframework.ui.Model;


@RestController
@RequestMapping("/admin")
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
    @Autowired
    private PostRepository postRepository;

    @ApiOperation(value = "멤버 리스트 불러옴 admin페이지에서 members 페이지로 이동할때")
    @Operation(summary = "members리스트 뽑는다", description = "\n admin페이지->members페이지-> 리스트 리턴")
    @GetMapping ("/members")
    public ResponseEntity<List<Member>> memberList() {
        List<Member> members = memberService.findMembers();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }




    @ApiOperation(value = "멤버페이지에서 회원 삭제 기능 ")
    @Operation(summary = "회원 삭제", description = "\n member_id 헤더로 받고 데이터베이스 비교 후 회원 삭제")
    @DeleteMapping("/members/{member_id}")
    public  ResponseEntity drop(@PathVariable Long member_id){
        if (memberRepository.existsById(member_id)) {
            memberRepository.deleteById(member_id);
            return new ResponseEntity<>("회원 삭제 성공", HttpStatus.OK);
        }
        return new ResponseEntity<>("해당 회원이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
    }

    @ApiOperation(value = "comments페이지에서 comment 리스트 불러옴")
    @Operation(summary = "comments리스트", description = "\n admin페이지 -> comments->페이지로 return하여 리스트 뽑")
    @GetMapping ("/comments")
    public ResponseEntity<List<Comment>> commentList() {
        List<Comment> comments = commentService.getAllComments();
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    //댓글 삭제
    @ApiOperation(value = "댓글 아이디와 멤버 아이디 비교 후 삭제 ")
    @Operation(summary = "댓글 삭제", description = "\n comment_id, member_id 헤더로 받고 데이터베이스 비교 후 회원 삭제")
    @DeleteMapping("/comments/{comment_id}/{member_id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long comment_id) {
        Comment comment = commentRepository.findById(comment_id)
                .orElseThrow(() -> new RuntimeException("부모 댓글이 존재하지 않습니다."));
        List<Comment> children = comment.getChildren();
        if (children != null) {
            for (Comment child : children) {
                commentRepository.delete(child);
            }
        }
        commentRepository.deleteById(comment_id);
        return ResponseEntity.ok("ok");
    }


   @ApiOperation(value = "게시물 리스트 불러옴 posts페이지에서")
   @Operation(summary = "게시글 리스트", description = "\n admin 페이지-> posts페이지-> return하여 post list 출력")
   @GetMapping("/posts")
   public ResponseEntity<List<Post>> postList() {
       List<Post> posts = postService.getAllPostList();
       return new ResponseEntity<>(posts, HttpStatus.OK);
   }



    //게시글 삭제
    @ApiOperation(value = "포스트페이지에서 포스트 삭제 ")
    @Operation(summary = "게시글 삭제", description = "\n post_id 헤더로 받고 데이터베이스 비교 후 회원 삭제")
    @DeleteMapping("/posts/{post_id}")
    public ResponsePost deletePost(@PathVariable Long post_id) {
        postService.deletePost(post_id);
        ResponsePost responsePost = new ResponsePost(post_id, true);
        return responsePost;
    }
}
