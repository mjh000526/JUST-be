package com.example.just.Service;

import com.example.just.Dao.Member;
import com.example.just.Dao.Role;
import com.example.just.Dto.TokenDto;
import com.example.just.Repository.MemberRepository;
import com.example.just.jwt.TokenFilter;
import com.example.just.jwt.TokenProvider;
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
public class AppleService {
    @Autowired
    private MemberRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    public ResponseEntity loginApple(String id){
        String apple_email = this.userIdFromApple(id)+ "@apple.com";
        Member user = userRepository.findByEmail(apple_email);
        if(user == null){
            System.out.println("아이디없음");
            return new ResponseEntity<>("/api/apple/signup", HttpStatus.OK);
        }
        //jwt토큰생성
        String accesstoken = tokenProvider.createaccessToken(user);
        String refreshtoken = tokenProvider.createRefreshToken(user);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(TokenFilter.AUTHORIZATION_HEADER, "Bearer " + accesstoken);
        httpHeaders.add("refresh_token",refreshtoken);
        return new ResponseEntity<>(new TokenDto(accesstoken,refreshtoken), HttpStatus.OK);
    }

    public ResponseEntity signUpApple(String id,String nickname){
        String apple_email = this.userIdFromApple(id)+ "@apple.com";
        Member user = userRepository.findByEmail(apple_email);
        if(nickname == null ) return new ResponseEntity<>("닉네임을 입력해 주세요", HttpStatus.OK);
        else if(user == null){
            user = Member.builder()
                    .email(this.userIdFromApple(id)+ "@apple.com") //id토큰으로 email제작
                    .provider("apple")
                    .provider_id(this.userIdFromApple(id))//apple고유 id
                    .nickname(nickname)
                    .authority(Role.ROLE_USER)
                    .blameCount(0)
                    .blamedCount(0)
                    .build();
            userRepository.save(user);

            //jwt토큰생성
            String accesstoken = tokenProvider.createaccessToken(user);
            String refreshtoken = tokenProvider.createRefreshToken(user);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(TokenFilter.AUTHORIZATION_HEADER, "Bearer " + accesstoken);
            httpHeaders.add("refresh_token",refreshtoken);
            return new ResponseEntity<>(new TokenDto(accesstoken,refreshtoken), HttpStatus.OK);
        }
        return new ResponseEntity<>("이미 회원가입되어있는 유저입니다.", HttpStatus.OK);
    }

    //id토큰으로 고유번호를 추출해서 email제작
    public String userIdFromApple(String idToken) {
        StringBuffer result = new StringBuffer();
        try {
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
        JsonParser parser = new JsonParser();
        JsonObject keys = (JsonObject) parser.parse(result.toString());
        JsonArray keyArray = (JsonArray) keys.get("keys");

        //클라이언트로부터 디코딩
        String[] decodeArray = idToken.split("\\.");
        String header = new String(Base64.getDecoder().decode(decodeArray[0]));

        //kid값 확인
        JsonElement kid = ((JsonObject) parser.parse(header)).get("kid");
        JsonElement alg = ((JsonObject) parser.parse(header)).get("alg");

        //element
        JsonObject avaliableObject = null;
        for (int i = 0; i < keyArray.size(); i++) {
            JsonObject appleObject = (JsonObject) keyArray.get(i);
            JsonElement appleKid = appleObject.get("kid");
            JsonElement appleAlg = appleObject.get("alg");
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
