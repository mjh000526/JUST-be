package com.example.just.Service;

import com.example.just.Dao.Role;
import com.example.just.Dao.Member;
import com.example.just.Dto.MemberDto;
import com.example.just.Dto.ResponseMemberDto;
import com.example.just.Repository.MemberRepository;
import com.example.just.jwt.JwtFilter;
import com.example.just.jwt.JwtProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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
    JwtProvider jwtProvider;

    //카카오 토큰으로 카카오로부터 토큰발급(로그인)
    public ResponseEntity loginKakao(String token) throws IOException{
        String accessToken = null;
        String refreshToken = null;
        Member userbyEmail = null;
        try{
            //카카오토큰으로
            MemberDto user = getKakaoUser(token);
            userbyEmail = userRepository.findByEmail(user.getEmail());
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
        userbyEmail = Member.builder()
                .id(userbyEmail.getId())
                .email(userbyEmail.getEmail())
                .provider(userbyEmail.getProvider())
                .provider_id(userbyEmail.getProvider_id())
                .authority(Role.ROLE_USER)
                .nickname(userbyEmail.getNickname())
                .blameCount(userbyEmail.getBlameCount())
                .blamedCount(userbyEmail.getBlamedCount())
                .refreshToken(refreshToken)
                .build();
        userRepository.save(userbyEmail);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
        httpHeaders.add("refresh_token",refreshToken);
        ResponseMemberDto responseMemberDto = new ResponseMemberDto(userbyEmail.getEmail(),userbyEmail.getNickname());
        return ResponseEntity.ok().headers(httpHeaders).body(responseMemberDto);
    }

    //카카오 토큰으로 회원가입
    public ResponseEntity signUpKakao(String token, String nickname){
        String accesstoken = null;
        String refreshtoken = null;
        Member userbyEmail = null;
        try{
            //카카오토큰으로
            MemberDto user = getKakaoUser(token);
            userbyEmail = userRepository.findByEmail(user.getEmail());
            //DB에 없는 사용자라면 회원가입 처리
            if(userbyEmail == null){
                userbyEmail = Member.builder()
                        .email(user.getEmail())
                        .provider(user.getProvider())
                        .provider_id(user.getProvider_id())
                        .authority(Role.ROLE_USER)
                        .nickname(nickname)
                        .blameCount(0)
                        .blamedCount(0)
                        .build();
                userbyEmail = userRepository.save(userbyEmail);
                accesstoken = jwtProvider.createaccessToken(userbyEmail);
                refreshtoken = jwtProvider.createRefreshToken(userbyEmail);
                userbyEmail.setRefreshToken(refreshtoken);
                userRepository.save(userbyEmail);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accesstoken);
                httpHeaders.add("refresh_token",refreshtoken);
                ResponseMemberDto responseMemberDto = new ResponseMemberDto(userbyEmail.getEmail(),userbyEmail.getNickname());
                return ResponseEntity.ok().headers(httpHeaders).body(responseMemberDto);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        //jwt토큰생성

        return new ResponseEntity<>("이미 회원가입되어있는 유저입니다.", HttpStatus.OK);

    }
    //카카오토큰 페이로드
    public MemberDto getKakaoUser(String token) throws IOException{
        String host = "https://kapi.kakao.com/v2/user/me";
        String id;
        String email = "";
        MemberDto user = null;
        //access_token을 이용해 사용자 정보 조회
        try{
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

            //userDto 생성후 리턴
            JsonParser parser = new JsonParser();
            JsonElement elem = parser.parse(result);
            id = elem.getAsJsonObject().get("id").getAsString();
            email = elem.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            user = MemberDto.builder().provider_id(id).email(email).provider("kakao").build();
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return user;
    }

    public String getToken(String code) throws IOException{
        String host = "https://kauth.kakao.com/oauth/token";
        URL url = new URL(host);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String token = "";
        try {
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true); // 데이터 기록 알려주기

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=55ec14b78e17e978a4a3b64971060784");
            sb.append("&redirect_uri=http://43.201.174.163:8080/api/kakao/access_token");
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
            System.out.println("result = " + result);

            // json parsing
            JSONParser parser = new JSONParser();
            JSONObject elem = (JSONObject) parser.parse(result);

            String access_token = elem.get("access_token").toString();
            String refresh_token = elem.get("refresh_token").toString();
            System.out.println("refresh_token = " + refresh_token);
            System.out.println("access_token = " + access_token);

            token = access_token;
            System.out.println("여기까지 완료");
            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return token;
    }
    //닉네임 변경
    public ResponseEntity changeNickname(HttpServletRequest request,String nickname){
        String token = jwtProvider.getAccessToken(request);
        String id = jwtProvider.getIdFromToken(token);
        Member member = userRepository.findById(Long.valueOf(id)).get();
        if(member.getNickname().equals(nickname)) return new ResponseEntity<>("이미 같은 닉네임",HttpStatus.OK);
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
