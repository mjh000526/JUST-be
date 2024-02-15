package com.example.just.Dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Data;

import java.util.ArrayList;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GptDto {
    private String model;
    private List<Message> messages;
    private int temperature;
    private int maxTokens;
    private int topP;
    private int frequencyPenalty;
    private int presencePenalty;

    public GptDto(String model
            , String prompt
            , int temperature
            , int maxTokens
            , int topP
            , int frequencyPenalty
            , int presencePenalty) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", prompt+"\n 위 글에 대한 해시태그 3개만 만들어줘. 명사로만"));
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.topP = topP;
        this.frequencyPenalty = frequencyPenalty;
        this.presencePenalty = presencePenalty;

    }
}
