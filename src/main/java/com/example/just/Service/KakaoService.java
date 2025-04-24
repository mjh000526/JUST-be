package com.example.just.Service;

import com.example.just.Dao.Role;
import com.example.just.Dao.Member;
import com.example.just.Dto.MemberDto;
import com.example.just.Response.ResponseMemberDto;
import com.example.just.Repository.MemberRepository;
import com.example.just.jwt.JwtFilter;
import com.example.just.jwt.JwtProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class KakaoService {

    @Autowired
    private MemberRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Value("${local-add}")
    private String address;

    //카카오 토큰으로 카카오로부터 토큰발급(로그인)
    public ResponseEntity loginKakao(String token,String fcmToken) throws IOException{
        String accessToken = null;
        String refreshToken = null;
        Member userbyEmail = null;
        try{
            //카카오토큰으로 유저정보를 가진 객체 생성
            MemberDto user = getKakaoUser(token);
            userbyEmail = userRepository.findByEmail(user.getProvider_id()+"@kakao.com");
            //DB에 없는 사용자라면 회원가입 처리
            if(userbyEmail == null){
                return new ResponseEntity<>("/api/kakao/signup", HttpStatus.OK);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //jwt토큰생성
        accessToken = jwtProvider.createaccessToken(userbyEmail);
        refreshToken = jwtProvider.createRefreshToken(userbyEmail);
//        userbyEmail = Member.builder()
//                .id(userbyEmail.getId())
//                .email(userbyEmail.getEmail())
//                .provider(userbyEmail.getProvider())
//                .provider_id(userbyEmail.getProvider_id())
//                .authority(Role.ROLE_USER)
//                .nickname(userbyEmail.getNickname())
//                .blameCount(userbyEmail.getBlameCount())
//                .blamedCount(userbyEmail.getBlamedCount())
//                .refreshToken(refreshToken)
//                .build();
        System.out.println("리프레시 업데이트전");
        userbyEmail.setRefreshToken(refreshToken);
        userRepository.save(userbyEmail);
//        userbyEmail.setRefreshToken(refreshToken);
        System.out.println("리프레시 업데이트완료");
        HttpHeaders httpHeaders = new HttpHeaders();
        //응답 헤더에 해당 액세스 토큰 적재
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
        httpHeaders.add("refresh_token",refreshToken);
        //클라이언트에게 보여줄 응답 포맷에 맞게 변환
        ResponseMemberDto responseMemberDto = new ResponseMemberDto(userbyEmail.getEmail(),userbyEmail.getNickname());
        return ResponseEntity.ok().headers(httpHeaders).body(responseMemberDto);
    }

    //카카오 토큰으로 회원가입
    public ResponseEntity signUpKakao(String token, String fcmToken, String nickname){
        String accesstoken = null;
        String refreshtoken = null;
        Member userbyEmail = null;
        try{
            //카카오토큰으로
            MemberDto user = getKakaoUser(token);
            userbyEmail = userRepository.findByEmail(user.getProvider_id()+"@kakao.com");
            //DB에 없는 사용자라면 회원가입 처리
            if(userbyEmail == null){
                userbyEmail = Member.builder()
                        .email(user.getProvider_id()+"@kakao.com")
                        .provider(user.getProvider())
                        .provider_id(user.getProvider_id())
                        .authority(Role.ROLE_USER)
                        .nickname(nickname)
                        .fcmToken(fcmToken)
                        .blameCount(0)
                        .blamedCount(0)
                        .build();
                userbyEmail = userRepository.save(userbyEmail);
                accesstoken = jwtProvider.createaccessToken(userbyEmail);
                refreshtoken = jwtProvider.createRefreshToken(userbyEmail);
                userbyEmail.setRefreshToken(refreshtoken);
                userRepository.save(userbyEmail);
                HttpHeaders httpHeaders = new HttpHeaders();
                //응답헤더에 토큰 적재
                httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accesstoken);
                httpHeaders.add("refresh_token",refreshtoken);
                //클라이언트에게 보여줄 응답 포맷에 맞게 변환
                ResponseMemberDto responseMemberDto = new ResponseMemberDto(userbyEmail.getEmail(),userbyEmail.getNickname());
                return ResponseEntity.ok().headers(httpHeaders).body(responseMemberDto);
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        //해당 정보가 이미 있을 경우 해당 문자열 발송
        return new ResponseEntity<>("이미 회원가입되어있는 유저입니다.", HttpStatus.OK);

    }
    //카카오토큰 페이로드
    public MemberDto getKakaoUser(String token) throws IOException{
        //카카오 응답서버 url주소
        String host = "https://kapi.kakao.com/v2/user/me";
        String id;
        String email = "";
        MemberDto user = null;
        //access_token을 이용해 사용자 정보 조회
        try{
            //카카오토큰값으로 해당 url에 회원 정보 요청
            URL url = new URL(host);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization","Bearer "+token);//헤더에 토큰보내기

            int responseCode = conn.getResponseCode();

            //JSON타입 메세지 읽기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";
            while ((line = br.readLine())!=null){
                result +=line;
            }

            //인증서버에서 받은 result값을 json형식으로 변환
            JsonParser parser = new JsonParser();
            JsonElement elem = parser.parse(result);
            //응답 데이터의 회원id값 추출
            id = elem.getAsJsonObject().get("id").getAsString();
            user = MemberDto.builder().provider_id(id).email(email).provider("kakao").build();
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return user;
    }

    public String getToken(String code) throws IOException{
        //카카오 인증서버 url주소
        String host = "https://kauth.kakao.com/oauth/token";
        URL url = new URL(host);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String token = "";
        try {
            //해당 url로 인증 요청
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true); // 데이터 기록 알려주기
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=a70139967800d8bf5a148321b7aa70c0");
            sb.append("&redirect_uri=http://"+address+":9000/api/kakao/access_token");
            sb.append("&code=" + code);

            bw.write(sb.toString());
            bw.flush();

            int responseCode = urlConnection.getResponseCode();
            System.out.println("responseCode = " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line = "";
            String result = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }

            // json parsing
            JSONParser parser = new JSONParser();
            JSONObject elem = (JSONObject) parser.parse(result);
            //응답받은 데이터에서 token값 추출
            String access_token = elem.get("access_token").toString();
            String refresh_token = elem.get("refresh_token").toString();

            token = access_token;
            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //토큰값 반환
        return token;
    }
    //닉네임 변경
    public ResponseEntity changeNickname(HttpServletRequest request,String nickname){
        //헤더의 토큰으로 클라이언트 id 추출
        String token = jwtProvider.getAccessToken(request);
        String id = jwtProvider.getIdFromToken(token);

        //해당 id로 회원 정보 조회
        Member member = userRepository.findById(Long.valueOf(id)).get();
        //요청받은 닉네임이 이미 존재하는 닉네임일 경우
        if(member.getNickname().equals(nickname)) return new ResponseEntity<>("이미 같은 닉네임",HttpStatus.OK);

        //기존의 회원 정보에 변경할 닉네임을 담은 객체 생성
        Member saveMember = Member.builder()
                .id(member.getId())
                .authority(member.getAuthority())
                .blamedCount(member.getBlamedCount())
                .blameCount(member.getBlameCount())
                .createTime(member.getCreateTime())
                .email(member.getEmail())
                .provider_id(member.getProvider_id())
                .provider(member.getProvider())
                .refreshToken(member.getRefreshToken())
                .nickname(nickname).build();
        userRepository.save(saveMember);
        return new ResponseEntity<>("닉네임 변경",HttpStatus.OK);
    }
    //멤버조회(테스트용)
    public ResponseEntity info(){
        List<Member> list = userRepository.findAll();
        return new ResponseEntity<>(list,HttpStatus.OK);
    }
}
