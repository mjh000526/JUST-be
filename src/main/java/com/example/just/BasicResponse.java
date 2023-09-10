package com.example.just;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasicResponse {
    private Integer code;
    private HttpStatus httpStatus;
    private String message;
    private String accessToken;
    private String refreshToken;
    private Integer count;
    private List<Object> result;
}
/*

 */
/*
dssa
 */