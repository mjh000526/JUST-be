package com.example.just.Service;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dto.CommentDto;
import com.example.just.Dto.CommentResponseDto;
import com.example.just.Dto.PutCommentDto;
import com.example.just.Dto.ResponseGetMemberCommentDto;
import com.example.just.Repository.CommentRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.example.just.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtProvider jwtProvider;
    public Comment createComment(Long postId, Long member_id, CommentDto commentDto) {
        // 부모 댓글이 있는 경우, 해당 부모 댓글을 가져옴
        Comment parentComment = null;
        if (commentDto.getParent_comment_id() != null && commentDto.getParent_comment_id() != 0) {
            parentComment = commentRepository.findById(commentDto.getParent_comment_id())
                    .orElseThrow(() -> new RuntimeException("부모 댓글이 존재하지 않습니다."));
        }

        // 게시물이 있는지 확인하고 가져옴
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));

        Member member = memberRepository.findById(member_id).orElseGet(() -> new Member());

        Comment comment = new Comment();
        comment.setComment_content(commentDto.getComment_content());
        comment.setPost(post);
        comment.setMember(member);
        comment.setParent(parentComment);
        comment.setComment_like(0L);
        comment.setComment_dislike(0L);
        LocalDateTime currentDateTime = LocalDateTime.now();
        Date currentTime = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
        comment.setComment_create_time(currentTime);
        comment.setBlamedCount(0);
        Optional<Member> receiver = memberRepository.findById(postRepository.findById(postId).get().getMember().getId());
        // 부모 댓글이 있을 경우, 자식 댓글로 추가
        if (parentComment != null) {
            parentComment.getChildren().add(comment);
            notificationService.send(receiver.get(), "bigComment", parentComment.getComment_id(), member_id);

        }else if(parentComment == null){
            notificationService.send(receiver.get(), "comment", post.getPost_id(), member_id);
        }


        return commentRepository.save(comment);
    }

    public List<CommentResponseDto> getCommentList(Long postId, HttpServletRequest req) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));
        Long member_id;
        if(req.getHeader("Authorization")!=null){
            String token = jwtProvider.getAccessToken(req);
            member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        }else member_id = 0L;


        return post.getComments().stream()
                .map(comment -> new CommentResponseDto(comment,member_id))
                .collect(Collectors.toList());
    }

    public ResponseEntity<String> deleteComment(Long postId, Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("부모 댓글이 존재하지 않습니다."));
        comment.setChildren(null);
        commentRepository.deleteById(commentId);
        return ResponseEntity.ok("ok");
    }

    public ResponseEntity<String> putComment(Long postId, Long commentId,
                                             PutCommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 존재하지 않습니다: " + commentId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));

        comment.setComment_content(commentDto.getComment_content());
        // 업데이트된 댓글을 저장합니다.
        commentRepository.save(comment);

        return ResponseEntity.ok("ok");
    }

    public ResponseEntity<String> blameComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 존재하지 않습니다: " + commentId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));

        comment.setBlamedCount(comment.getBlamedCount()+1);
        commentRepository.save(comment);

        return ResponseEntity.ok("ok");
    }

    public int blameGetComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 존재하지 않습니다: " + commentId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));
        return comment.getBlamedCount();
    }

    @Transactional
    public void likeComment(Long postId, Long commentId, Long member_id) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글이 존재하지 않습니다: " + commentId));
        Member member = memberRepository.findById(member_id).orElseGet(() -> new Member());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));
        if (comment.getLikedMembers().contains(member)) {
            comment.removeLike(member);
        } else {
            comment.addLike(member);
        }
        notificationService.send(post.getMember(), "commentLike", post.getPost_id(), member_id);
       commentRepository.save(comment);

    }

    public List<ResponseGetMemberCommentDto> getMyComment(Long member_id) {
        List<Comment> comments = commentRepository.findAll();
        List<ResponseGetMemberCommentDto> getMemberCommentDtos = new ArrayList<>();
        for (int i=0; i<comments.size(); i++) {
            ResponseGetMemberCommentDto getMemberCommentDto = new ResponseGetMemberCommentDto();
            if (comments.get(i).getMember().getId()==member_id) {
                getMemberCommentDto.setComment_content(comments.get(i).getComment_content());
                getMemberCommentDto.setTime(comments.get(i).getComment_create_time());
                getMemberCommentDto.setPost_id(comments.get(i).getPost().getPost_id());
                getMemberCommentDtos.add(getMemberCommentDto);
            }
        }
        return getMemberCommentDtos;
    }
}

