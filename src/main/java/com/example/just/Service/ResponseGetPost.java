package com.example.just.Service;

import java.util.List;
import lombok.Getter;

@Getter
public class ResponseGetPost<T> {
    private List<T> mySlice;
    private boolean hasNext;

    public ResponseGetPost(List<T> results, boolean hasNext) {
        this.mySlice = results;
        this.hasNext = hasNext;
    }
}
