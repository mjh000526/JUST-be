package com.example.just.Service;

import com.example.just.Dao.Member;
import com.example.just.Dao.Role;
import com.example.just.Response.ResponseMemberDto;
import com.example.just.Repository.MemberRepository;
import com.example.just.jwt.JwtFilter;
import com.example.just.jwt.JwtProvider;
import com.google.gson.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AppleService {//애플 로그인 기능 서비스
    @Autowired
    private MemberRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    //애플 로그인 서비스
    public ResponseEntity loginApple(String id,String fcmToken){
        String apple_email = this.userIdFromApple(id)+ "@apple.com";//apple 토큰값으로 찾은 회원고유 id
        Member user = userRepository.findByEmail(apple_email);//해당 이메일값으로 회원 정보 조회
        if(user == null){//회원가입하지 않은 회원일 시 예외처리
            return new ResponseEntity<>("/api/apple/signup", HttpStatus.OK);
        }
        //jwt토큰생성
        String accesstoken = jwtProvider.createaccessToken(user);
        String refreshtoken = jwtProvider.createRefreshToken(user);
        user.setRefreshToken(refreshtoken);
        user.setFcmToken(fcmToken);
        userRepository.save(user); //refresh토큰 재발급 후 update
        HttpHeaders httpHeaders = new HttpHeaders();
        //응답헤더에 토큰 적재
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accesstoken);
        httpHeaders.add("refresh_token",refreshtoken);
        //회원 정보를 응답포맷에 맞게 변환
        ResponseMemberDto responseMemberDto = new ResponseMemberDto(user.getEmail(),user.getNickname());
        return ResponseEntity.ok().headers(httpHeaders).body(responseMemberDto);
    }

    public ResponseEntity signUpApple(String id,String fcmToken, String nickname){
        String apple_email = this.userIdFromApple(id)+ "@apple.com";//애플 토큰으로 인증 후 이메일 생성
        Member user = userRepository.findByEmail(apple_email);//해당 이메일로 회원정보 조회
        HttpHeaders httpHeaders = new HttpHeaders();
        //클라이언트가 닉네임을 적지 않을 경우 예외처리
        if(nickname == null ) return new ResponseEntity<>("닉네임을 입력해 주세요", HttpStatus.OK);
        else if(user == null){//디비에 회원정보가 없을 시
            user = Member.builder()//클라이언트의 데이터를 기반으로 회원객체생성
                    .email(this.userIdFromApple(id)+ "@apple.com") //id토큰으로 email제작
                    .provider("apple")
                    .provider_id(this.userIdFromApple(id))//apple고유 id
                    .nickname(nickname)
                    .authority(Role.ROLE_USER)
                    .fcmToken(fcmToken)
                    .blameCount(0)
                    .blamedCount(0)
                    .build();
            //신규저장 create
            user = userRepository.save(user);

            //jwt토큰생성
            String accesstoken = jwtProvider.createaccessToken(user);
            String refreshtoken = jwtProvider.createRefreshToken(user);
            user.setRefreshToken(refreshtoken);
            userRepository.save(user);

            //응답헤더에 토큰 적재
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accesstoken);
            httpHeaders.add("refresh_token",refreshtoken);
        }
        //응답포맷에 맞게 변환 후 응답
        ResponseMemberDto responseMemberDto = new ResponseMemberDto(user.getEmail(),user.getNickname());
        return ResponseEntity.ok().headers(httpHeaders).body(responseMemberDto);
    }

    //id토큰으로 고유번호를 추출해서 email제작
    public String userIdFromApple(String idToken) {
        StringBuffer result = new StringBuffer();
        try {
            //애플의 인증서버에 http통신을 연결하여 해당 id토큰의 정보로 인증요청
            URL url = new URL("https://appleid.apple.com/auth/keys");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //url에 요청하고 받은 response를 json형식으로 변환
        JsonParser parser = new JsonParser();
        JsonObject keys = (JsonObject) parser.parse(result.toString());
        JsonArray keyArray = (JsonArray) keys.get("keys");

        //클라이언트로부터 디코딩
        String[] decodeArray = idToken.split("\\.");
        String header = new String(Base64.getDecoder().decode(decodeArray[0]));

        //kid값 확인
        JsonElement kid = ((JsonObject) parser.parse(header)).get("kid");
        JsonElement alg = ((JsonObject) parser.parse(header)).get("alg");

        //애플에서 주는 여러개의 인증키값을 반복하며 대조
        JsonObject avaliableObject = null;
        for (int i = 0; i < keyArray.size(); i++) {
            JsonObject appleObject = (JsonObject) keyArray.get(i);
            JsonElement appleKid = appleObject.get("kid");
            JsonElement appleAlg = appleObject.get("alg");
            //일치할 경우 인증객체 할당 후 탈출
            if (Objects.equals(appleKid, kid) && Objects.equals(appleAlg, alg)) {
                avaliableObject = appleObject;
                break;
            }

        }
        //일치하는 공개키가 없을 경우
        if (ObjectUtils.isEmpty(avaliableObject)) return "인증키이상";//new ResponseEntity<>("인증키가 이상함", HttpStatus.OK);
        PublicKey publicKey = this.getPublicKey(avaliableObject);

        Claims userInfo = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(idToken).getBody();
        JsonObject userInfoObject = (JsonObject) parser.parse(new Gson().toJson(userInfo));
        JsonElement appleAlg = userInfoObject.get("sub");
        String userId = appleAlg.getAsString();
        return userId;
    }

    //애플에서 제공하는 공개키값 디코딩
    public  PublicKey getPublicKey(JsonObject object){
        String nStr = object.get("n").toString();
        String eStr = object.get("e").toString();
        byte[] nBytes = Base64.getUrlDecoder().decode(nStr.substring(1,nStr.length()-1));
        byte[] eBytes = Base64.getUrlDecoder().decode(eStr.substring(1,eStr.length()-1));

        BigInteger n = new BigInteger(1,nBytes);
        BigInteger e = new BigInteger(1,eBytes);
        try{
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n,e);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        } catch (InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }
    }
}
