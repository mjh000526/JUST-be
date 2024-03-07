package com.example.just.Response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseMessage {
    private String message;
    public ResponseMessage(String m){
        message = m;
    }
}
