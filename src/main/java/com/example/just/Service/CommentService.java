package com.example.just.Service;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dto.CommentDto;
import com.example.just.Repository.CommentRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;

    public Comment createComment(Long postId, Long member_id, CommentDto commentDto) {
        // 부모 댓글이 있는 경우, 해당 부모 댓글을 가져옴
        Comment parentComment = null;
        if (commentDto.getParentCommentId() != null) {
            parentComment = commentRepository.findById(commentDto.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("부모 댓글이 존재하지 않습니다."));
        }

        // 게시물이 있는지 확인하고 가져옴
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));

        Member member = memberRepository.findById(member_id).orElseGet(Member::new);

        Comment comment = new Comment();
        comment.setComment_content(commentDto.getComment_content());
        comment.setPost(post);
        comment.setMember(member);
        comment.setParent(parentComment);
        comment.setComment_create_time(LocalDateTime.now());

        // 부모 댓글이 있을 경우, 자식 댓글로 추가
        if (parentComment != null) {
            parentComment.getChildren().add(comment);
        }

        return commentRepository.save(comment);
    }
    public List<Comment> getCommentList(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않습니다."));

        return post.getComments();
    }

}

