package com.example.just.Admin;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dto.MemberDto;
import com.example.just.Dto.PostDto;
import com.example.just.Repository.CommentRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
import com.example.just.Service.ResponsePost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.just.Service.MemberService;
import com.example.just.Service.CommentService;
import com.example.just.Service.PostService;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/admin")
@SpringBootApplication
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


    @GetMapping ("/members")
    public String memberList(Model model){
        List<MemberDto> members =memberService.findMembers();
        model.addAttribute("members",members);
        return"/admin/members";
    }


    //회원 삭제
    @DeleteMapping("/members/{member_id}")
    public  ResponseEntity drop(@PathVariable Long member_id){
        if (memberRepository.existsById(member_id)) {
            memberRepository.deleteById(member_id);
            return new ResponseEntity<>("회원 삭제 성공", HttpStatus.OK);
        }
        return new ResponseEntity<>("해당 회원이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
    }

    @GetMapping ("/comments")
    public String CommentList(Model model){
        List<Comment> comments = commentService.getAllComments();
        model.addAttribute("comments", comments);
        return "/admin/comments";

    }

    //댓글 삭제
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

   @GetMapping("/posts")
   public String PostList(Model model){
        List<Post> posts =postService.getAllPostList();
        model.addAttribute("posts",posts);
        return"/admin/posts";
   }
    //게시글 삭제
    @DeleteMapping("/posts/{post_id}")
    public ResponsePost deletePost(@PathVariable Long post_id) {
        postService.deletePost(post_id);
        ResponsePost responsePost = new ResponsePost(post_id, "삭제 완료");
        return responsePost;
    }
}
