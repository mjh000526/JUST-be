package com.example.just.Service;

import com.example.just.Dao.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponsePost {
    private Post post;
    private boolean value;

    public ResponsePost(Post post, boolean b) {
        this.post =post;
        this.value =b;
    }
}
