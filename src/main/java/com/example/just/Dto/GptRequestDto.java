package com.example.just.Dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Data;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GptRequestDto {
    private String prompt;
    // 기본 생성자 추가
    public GptRequestDto() {
    }
    public GptRequestDto(String prompt) {
        this.prompt = prompt;
    }
}
