package com.example.just.Service;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dto.CommentDto;
import com.example.just.Dto.PutCommentDto;
import com.example.just.Repository.CommentRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    public Comment createComment(Long postId, Long member_id, CommentDto commentDto) {
        // 부모 댓글이 있는 경우, 해당 부모 댓글을 가져옴
        Comment parentComment = null;
        if (commentDto.getParentCommentId() != null && commentDto.getParentCommentId() != 0) {
            parentComment = commentRepository.findById(commentDto.getParentCommentId())
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
        comment.setComment_create_time(LocalDateTime.now());
        comment.setBlamedCount(0);

        // 부모 댓글이 있을 경우, 자식 댓글로 추가
        if (parentComment != null) {
            parentComment.getChildren().add(comment);

        }
        Optional<Member> receiver = memberRepository.findById(postRepository.findById(postId).get().getMember().getId());
        notificationService.send(receiver.get(), "comment", post.getPost_id(), member_id);
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentList(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));

        return post.getComments();
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
}

