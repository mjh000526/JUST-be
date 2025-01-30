package com.example.just.Service;

import com.example.just.Controller.GptController;
import com.example.just.Dto.DenyListDto;
import com.example.just.Dto.GptDto;
import com.example.just.Dto.GptNerDto;
import com.example.just.Dto.GptRequestDto;
import com.example.just.Dto.GptResponseDto;
import com.example.just.Dto.Message;
import com.example.just.Dto.PostContentDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GptService {
    @Value("${gpt.model}")
    private String model;

    @Value("${gpt.api.url}")
    private String apiUrl;

    @Value("${server-add}")
    private String serverAddress;

    @Autowired
    private final RestTemplate restTemplate;

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

        System.out.println("GPT 태그는 : " + content);
        for (String word : splitContent) {
            if (!word.trim().isEmpty()) { // 문자열이 공백이 아닌 경우에만 추가
                message.add(word.trim()); // 해시태그 추가 시 #을 포함하여 추가
            }
        }
        return message;
    }

    public String gptner(PostContentDto content){
        String str = String.join("<just>", content.getContent());
        GptNerDto requestBody = new GptNerDto(model,true,
                Arrays.asList(new Message(
                        "user",
                        str + "\n" +
                                "위 문장에서 사람 이름, 회사 이름, 조직 이름을 찾고, 해당 단어를 'ㅇㅇㅇ'으로 대체해주세요. " +
                                "공백이나 다른 문장은 그대로 유지해주세요."
                )));
        GptResponseDto gptResponse = restTemplate.postForObject(
                apiUrl
                , requestBody
                , GptResponseDto.class
        );
        System.out.println(gptResponse.getChoices().get(0).getMessage().getContent());
        return gptResponse.getChoices().get(0).getMessage().getContent();
    }

    public DenyListDto getPororo(PostContentDto content) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(content);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(requestBody,headers);

        ResponseEntity<DenyListDto> responseEntity = restTemplate.exchange(
                "http://" + serverAddress + ":8081/api/ner/post",
                HttpMethod.POST,
                request,
                DenyListDto.class);

        DenyListDto responseBody = responseEntity.getBody();
        return responseBody;
    }


}