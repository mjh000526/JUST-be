package com.example.just.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FCMMessageDto {
    private boolean validateOnly;
    private FCMMessage message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class FCMMessage{
        private Notification notification;
        private String token;
        private Data data;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification{
        private String title;
        private String body;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data{
        private String name;
        private String description;
    }

}
