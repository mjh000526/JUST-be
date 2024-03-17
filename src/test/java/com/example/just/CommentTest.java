package com.example.just;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.just.Dao.Comment;
import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dao.Role;
import com.example.just.Dto.CommentDto;
import com.example.just.Dto.PutCommentDto;
import com.example.just.Response.ResponseMyCommentDto;
import com.example.just.Response.ResponsePostCommentDto;
import com.example.just.Repository.CommentRepository;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;
import com.example.just.Service.CommentService;
import com.example.just.jwt.JwtFilter;
import com.example.just.jwt.JwtProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CommentTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private CommentService commentService;

    private Post post;

    private Member member;

    @BeforeEach
    public void setUpPost() {
        MockitoAnnotations.initMocks(this);

        member = Member.builder()
                .id(1L)
                .email("mjho000526@naver.com")
                .provider("kakao")
                .provider_id("")
                .authority(Role.ROLE_USER)
                .nickname("테스트닉네임")
                .blameCount(0)
                .blamedCount(0)
                .build();
        List<String> list = new ArrayList<>();
        list.add("테스트용 컨텐트");
        post = Post.builder()
                .post_id(1L)
                .postContent(list)
                .post_like(0L)
                .blamedCount(0L)
                .post_like(0L)
                .likedMembers(new ArrayList<>())
                .comments(new ArrayList<>())
                .member(member).build();
        memberRepository.save(member);
        postRepository.save(post);
    }

    //댓글 기능 테스트전 임시데이터 저장
    private Comment inputComment(Comment comment) {
        comment = new Comment();
        comment.setComment_id(1L);
        comment.setComment_content("테스트댓글");
        comment.setMember(member);
        comment.setPost(post);
        comment.setParent(null);
        comment.setComment_like(0L);
        commentRepository.save(comment);
        return comment;
    }

    @Test
    @DisplayName("댓글 생성")
    public void PostComment() {
        //given
        Comment comment = new Comment();
        comment.setComment_content("테스트댓글");
        comment.setParent(null);
        when(jwtProvider.getAccessToken(Mockito.any())).thenCallRealMethod();
        when(jwtProvider.getIdFromToken(Mockito.any())).thenReturn("1");
        when(postRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(post));
        when(memberRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(member));
        when(commentRepository.save(Mockito.any())).thenReturn(comment);

        //when
        Comment result = commentService.createComment(post.getPost_id(), member.getId(), new CommentDto(comment));

        //then
        Assertions.assertEquals(result.getComment_content(), "테스트댓글");
    }

    //존재하지 않는 회원오류 추가해야함(서비스에서 예외처리)

    @Test
    @DisplayName("존재하지 않는 게시물 오류")
    public void PostCommenterror() throws Exception {
        //given
        Comment comment = new Comment();
        comment.setComment_content("테스트댓글");
        comment.setParent(null);
        post.setPost_id(2L);
        when(jwtProvider.getAccessToken(Mockito.any())).thenCallRealMethod();
        when(jwtProvider.getIdFromToken(Mockito.any())).thenReturn("1");
        when(postRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        when(memberRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(member));
        when(commentRepository.save(Mockito.any())).thenReturn(comment);

        //when
        Throwable createException = Assertions.assertThrows(RuntimeException.class, () -> {
            commentService.createComment(post.getPost_id(), member.getId(), new CommentDto(comment));
        });
        Throwable applyException = Assertions.assertThrows(RuntimeException.class, () -> {
            commentService.createComment(post.getPost_id(), member.getId(), new CommentDto(comment));
        });

        //then
        Assertions.assertEquals("게시물이 존재하지 않습니다.", createException.getMessage());
        Assertions.assertEquals("게시물이 존재하지 않습니다.", applyException.getMessage());
    }

    @Test
    @DisplayName("댓글 수정")
    public void CommentApply() {
        //given
        Comment comment = null;
        comment = inputComment(comment);
        when(commentRepository.findById(Mockito.any())).thenReturn(Optional.of(comment));
        when(postRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(post));

        //when
        ResponseEntity<String> result = commentService.putComment(1L, 1L, new PutCommentDto("테스트용"));

        //then
        Assertions.assertEquals(result.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(result.getBody(), "ok");
    }

    @Test
    @DisplayName("수정할 댓글 존재하지 않음")
    public void CommentApplyCommentError() throws Exception {
        //given
        Comment comment = new Comment();
        comment.setComment_id(2L);
        comment.setComment_content("댓글 수정 오류");
        when(commentRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        when(postRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(post));

        //when
        Throwable exception = Assertions.assertThrows(NoSuchElementException.class, () -> {
            commentService.putComment(1L, 1L, new PutCommentDto("테스트용"));
        });

        //then
        Assertions.assertEquals("댓글이 존재하지 않습니다: 1", exception.getMessage());
    }

    @Test
    @DisplayName("댓글 조회 기능")
    public void getCommentList(){
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        Comment comment = new Comment();
        comment = inputComment(comment);
        post.getComments().add(comment);
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));
        when(postRepository.findById(2L)).thenReturn(Optional.of(new Post()));

        //when
        ResponsePostCommentDto result = commentService.getCommentList(1L,request);
        ResponsePostCommentDto emptyResult = commentService.getCommentList(2L,request);

        //then
        Assertions.assertEquals(result.getPost_content(),post.getPostContent());
        Assertions.assertEquals(result.getComments().size(),1);
        Assertions.assertEquals(emptyResult.getPost_content(),null);
    }

    @Test
    @DisplayName("댓글 좋아요 기능")
    public void likeComment(){
        //given
        Comment comment = null;
        comment = inputComment(comment);
        when(commentRepository.findById(Mockito.any())).thenReturn(Optional.of(comment));
        when(postRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(post));
        when(memberRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(member));

        //when
        commentService.likeComment(1L,1L, 1L);
        Assertions.assertEquals(comment.getComment_like(),1);

        //then
        comment.getLikedMembers().add(member);
        when(commentRepository.findById(Mockito.any())).thenReturn(Optional.of(comment));
        commentService.likeComment(1L,1L, 1L);
        Assertions.assertEquals(comment.getComment_like(),0L);
    }

    @Test
    @DisplayName("자신의 댓글 조회")
    public void getMyComments(){
        //given
        Comment comment = null;
        comment = inputComment(comment);
        List<Comment> comments = new ArrayList<>();
        for(int i = 0;i<5;i++) comments.add(comment);
        when(memberRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(member));
        when(commentRepository.findAllByMember(Mockito.any())).thenReturn(comments);

        //when
        ResponseEntity<List<ResponseMyCommentDto>> result = commentService.getMyComment(1L);

        //then
        Assertions.assertEquals(result.getStatusCode(),HttpStatus.OK);
        Assertions.assertEquals(result.getBody().size(),5);
    }
}
