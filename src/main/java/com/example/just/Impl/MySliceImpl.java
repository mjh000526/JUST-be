package com.example.just.Impl;

import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

@Getter
public class MySliceImpl<Post> extends SliceImpl<Post> {

    private String nextCursor;

    public MySliceImpl(List<Post> content, Pageable pageable, boolean hasNext, String nextCursor) {
        super(content, pageable, hasNext);
        this.nextCursor = nextCursor;
    }

    public String getNextCursor() {
        return nextCursor;
    }
}