package com.example.just.Service;

import com.example.just.Controller.GptController;
import com.example.just.Dto.GptDto;
import com.example.just.Dto.GptRequestDto;
import com.example.just.Dto.GptResponseDto;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GptService { // Gpt API 이용 관련
    // Gpt API 이용을 위한 모델 선정
    @Value("${gpt.model}")
    private String model;

    // Gpt API URL
    @Value("${gpt.api.url}")
    private String apiUrl;

    @Autowired
    private final RestTemplate restTemplate;

    // Gpt API를 이용하여 태그 추출
    public List<String> getTag(GptRequestDto prompt) {
        // Gpt API 요청
        GptDto request = new GptDto(
                model, prompt.getPrompt(), 1, 256, 1, 1, 2);
        GptResponseDto gptResponse = restTemplate.postForObject(
                apiUrl
                , request
                , GptResponseDto.class
        );
        // Gpt API 응답을 이용하여 태그 추출
        List<String> message = new ArrayList<>();
        // Gpt API 응답에서 태그 추출
        String content = gptResponse.getChoices().get(0).getMessage().getContent();
        // 태그 추출
        String[] splitContent = content.split("#");

        System.out.println("GPT 태그는 : " + content);
        // 태그 추출
        for (String word : splitContent) {
            if (!word.trim().isEmpty()) { // 문자열이 공백이 아닌 경우에만 추가
                message.add(word.trim()); // 해시태그 추가 시 #을 포함하여 추가
            }
        }
        // 태그 반환
        return message;
    }
}
