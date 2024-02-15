package com.example.just.Controller;

import com.example.just.Dto.GptDto;
import com.example.just.Dto.GptResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/gpt")
@RequiredArgsConstructor
public class GptController {

    @Value("${gpt.model}")
    private String model;

    @Value("${gpt.api.url}")
    private String apiUrl;
    @Autowired
    private final RestTemplate restTemplate;


    @Operation(summary = "Gpt를 통한 태그 생성 API", description = "태그 3개를 생성 List<String>형식으로 반환합니다."
            + "prompt안에는 글 내용을 넣으면 됩니다 형식 없음 그냥 복붙하면 됨.\n\n 많이 테스트하지 마세요!!!!! 돈 나와요! 연결이 잘 되는지 용도로만 확인 바람.")
    @GetMapping("/chat")
    public List<String> chat(@RequestBody String prompt) {

        GptDto request = new GptDto(
                model, prompt, 1, 256, 1, 1, 2);

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
                message.add(word);
            }
        }

        return message;
    }

}