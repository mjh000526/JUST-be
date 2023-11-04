package com.example.just.Impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class MySliceImpl<Post> extends SliceImpl<Post> {

    private String nextCursor;

    public MySliceImpl(List<Post> content,  boolean hasNext) {
        super(content);
        this.nextCursor = nextCursor;
    }

    public String getNextCursor() {
        return nextCursor;
    }
}