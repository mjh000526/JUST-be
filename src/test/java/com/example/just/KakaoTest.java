package com.example.just;

import static org.mockito.Mockito.when;

import com.example.just.Dao.Member;
import com.example.just.Dao.Role;
import com.example.just.Dto.ResponseMemberDto;
import com.example.just.Repository.MemberRepository;
import com.example.just.Service.KakaoService;
import com.example.just.jwt.JwtFilter;
import com.example.just.jwt.JwtProvider;
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
public class KakaoTest {

    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private KakaoService kakaoService;

    @Mock
    private JwtProvider jwtProvider;

    MockHttpServletRequest request;

    //카카오 토큰유효시간이 넘어선 안됨
    private static final String accessToken = "OOZB2FX9ysF36_Vzefb5Qbo9avu1K8dCeOkKKiVOAAABjQwGrVL_A_o_BVb6-Q";


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    //test member 객체
    public Member SignUpRequest() {
        return Member.builder()
                .id(1L)
                .email("mjho000526@naver.com")
                .provider("kakao")
                .provider_id("")
                .authority(Role.ROLE_USER)
                .nickname("테스트닉네임")
                .blameCount(0)
                .blamedCount(0)
                .build();
    }

    @Test
    @DisplayName("카카오 회원가입 성공")
    public void KakaoSignUp() throws Exception {
        //given
        String nickName = "테스트닉네임";
        when(memberRepository.findByEmail(Mockito.any())).thenReturn(null);
        when(memberRepository.save(Mockito.any())).thenReturn(SignUpRequest());

        //when
        ResponseEntity<ResponseMemberDto> member = kakaoService.signUpKakao(accessToken, nickName);

        //then
        Assertions.assertEquals(member.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(member.getBody().getEmail(), "mjho000526@naver.com");
        Assertions.assertEquals(member.getBody().getNickname(), nickName);
    }

    @Test
    @DisplayName("카카오 로그인 성공")
    public void KakaoSignInSuccess() throws Exception {
        //given
//        when(jwtProvider.createaccessToken(Mockito.any())).thenReturn("mockedAccessToken");
//        when(jwtProvider.createRefreshToken(Mockito.any())).thenReturn("mockedRefreshToken");
        when(memberRepository.findByEmail(Mockito.any())).thenReturn(SignUpRequest());
        when(memberRepository.save(Mockito.any())).thenReturn(SignUpRequest());
        kakaoService.signUpKakao(accessToken, "테스트닉네임");

        //when
        ResponseEntity<ResponseMemberDto> member = kakaoService.loginKakao(accessToken);

        //then
        Assertions.assertEquals(member.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(member.getBody().getEmail(), "mjho000526@naver.com");
        Assertions.assertEquals(member.getBody().getNickname(), "테스트닉네임");
    }

    @Test
    @DisplayName("가입되지 않은 회원 로그인")
    public void KakaoSignInfailed() throws Exception {

        when(memberRepository.findByEmail(Mockito.any())).thenReturn(null);

        //when
        ResponseEntity<String> member = kakaoService.loginKakao(accessToken);

        //then
        Assertions.assertEquals(member.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(member.getBody(), "/api/kakao/signup");
    }

    @Test
    @DisplayName("잘못된 카카오 토큰")
    public void KaKaoInvalidToken() throws Exception {
        //given
        String invalidToken = "asdf";

        //then
        Assertions.assertThrows(NullPointerException.class, () -> {
            kakaoService.loginKakao(invalidToken);
        });
    }

    @Test
    @DisplayName("닉네임 변경")
    public void KakaoChangeNickName() throws Exception {
        //given
        String changeNickname = "테코용";
        when(jwtProvider.getAccessToken(Mockito.any())).thenCallRealMethod();
        when(jwtProvider.getIdFromToken(Mockito.any())).thenReturn("1");
        when(memberRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(SignUpRequest()));
        when(memberRepository.save(Mockito.any())).thenReturn(SignUpRequest());
        request = new MockHttpServletRequest();
        //이건 가려지도록 해야할듯
        request.addHeader(JwtFilter.AUTHORIZATION_HEADER,
                "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiYXV0aCI6IlJPTEVfVVNFUiIsImF1ZCI6Im1qaG8wMDA1MjZAbmF2ZXIuY29tIiwiaWF0IjoxNzA0OTU4NDg1LCJleHAiOjE3MDc1NTA0ODV9.DlsaK55eSAeI1P9nQKzwwoQL3RxZlRu6PlZZcOhSM_xFDuC6lXnL6EIkbxoq0y7XVl9DlEKSLLOey-ULm-JRVw");

        //when
        ResponseEntity<String> result = kakaoService.changeNickname(request, changeNickname);

        //then
        Assertions.assertEquals(result.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(result.getBody(),"닉네임 변경");
    }
}
