package com.example.just.Response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseMessage { //에러 메세지를 응답으로 보여줄 때 사용되는 클래스
    private String message;
    public ResponseMessage(String m){
        message = m;
    }
}
