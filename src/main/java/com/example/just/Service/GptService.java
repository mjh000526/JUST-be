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
public class GptService {
    @Value("${gpt.model}")
    private String model;

    @Value("${gpt.api.url}")
    private String apiUrl;

    @Autowired
    private  final RestTemplate restTemplate;
    public List<String> getTag(GptRequestDto prompt) {
        GptDto request = new GptDto(
                model, prompt.getPrompt(), 1, 256, 1, 1, 2);

        GptResponseDto gptResponse = restTemplate.postForObject(
                apiUrl
                , request
                , GptResponseDto.class
        );
        List<String> message = new ArrayList<>();
        String content = gptResponse.getChoices().get(0).getMessage().getContent();
        String[] splitContent = content.split("#");

        for (String word : splitContent) {
            if (!word.isEmpty()) {
                if (message.size()!=2) {
                    word = word.substring(0, word.length() - 2);
                }
                message.add(word);
            }
        }
        return message;
    }
}
