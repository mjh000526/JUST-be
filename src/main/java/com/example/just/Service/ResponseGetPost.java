package com.example.just.Service;

import com.example.just.Dao.Post;
import com.example.just.Impl.MySliceImpl;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public class ResponseGetPost {
    private MySliceImpl<Post> mySlice;
    private boolean value;

    public <Post> ResponseGetPost(MySliceImpl<Post> posts, boolean b) {
        this.mySlice= (MySliceImpl<com.example.just.Dao.Post>) posts;
        this.value=b;
    }
}
