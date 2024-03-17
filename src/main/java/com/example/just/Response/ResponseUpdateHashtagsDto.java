package com.example.just.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseUpdateHashtagsDto {

    private Long id;
    private List<String> updatedHashtags;

    public ResponseUpdateHashtagsDto(Long id, List<String> updatedHashtags) {
        this.id = id;
        this.updatedHashtags = updatedHashtags;
    }
}