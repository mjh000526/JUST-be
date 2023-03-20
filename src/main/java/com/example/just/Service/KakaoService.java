package com.example.just.Service;

import com.example.just.Dao.Role;
import com.example.just.Dao.Member;
import com.example.just.Dto.MemberDto;
import com.example.just.Dto.TokenDto;
import com.example.just.Repository.MemberRepository;
import com.example.just.jwt.JwtProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
public class KakaoService {

    @Autowired
    private MemberRepository userRepository;

    @Autowired
    JwtProvider jwtProvider;

    //카카오 코드로 카카오로부터 토큰발급
    public ResponseEntity getToken(String code) throws IOException{
        String host = "https://kauth.kakao.com/oauth/token";
        URL url = new URL(host);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        String token="";
        HashMap<String,String> m = new HashMap<>();
        try{
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=55ec14b78e17e978a4a3b64971060784");
            sb.append("&redirect_uri=http://localhost:8080/member/kakao");
            sb.append("&code="+code);
            bw.write(sb.toString());
            bw.flush();

            int responseCode = urlConnection.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line ="";
            String result ="";
            while ((line= br.readLine())!=null){
                result+=line;
            }

            //제이슨파싱 - 카카오토큰받기기
            JSONParser parser = new JSONParser();
            JSONObject elem = (JSONObject) parser.parse(result);
            String access_token = elem.get("access_token").toString();

            //카카오토큰으로
            MemberDto user = getKakaoUser(access_token);
            Member userbyEmail = userRepository.findByEmail(user.getEmail());

            //DB에 없는 사용자라면 회원가입 처리
            if(userbyEmail == null){
                userbyEmail = Member.builder()
                        .email(user.getEmail())
                        .provider(user.getProvider())
                        .role(Role.ROLE_USER)
                        .build();
                userRepository.save(userbyEmail);
            }
            token = access_token;
            m.put("id",userbyEmail.getId().toString());
            br.close();
            bw.close();
        }catch (IOException e){
            e.printStackTrace();
        }catch (ParseException e){
            e.printStackTrace();
        }
        //jwt토큰생성
        String accesstoken = jwtProvider.generateToken(m);
        String refreshtoken = jwtProvider.generateRefreshToken(m);
        return new ResponseEntity<>(new TokenDto(accesstoken,refreshtoken), HttpStatus.OK);
    }
    //카카오토큰 페이로드
    public MemberDto getKakaoUser(String token) throws IOException{
        String host = "https://kapi.kakao.com/v2/user/me";
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
            email = elem.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            user = MemberDto.builder().email(email).provider("kakao").build();
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return user;
    }
}
