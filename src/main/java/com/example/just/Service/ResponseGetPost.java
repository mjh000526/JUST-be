package com.example.just.Service;

import com.example.just.Dao.Post;
import com.example.just.Dto.ResponseGetPostDto;
import com.example.just.Impl.MySliceImpl;
import java.util.List;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public class ResponseGetPost {
    private List<ResponseGetPostDto> mySlice;
    private boolean hasNext;



    public ResponseGetPost(List<ResponseGetPostDto> results, boolean b) {
        this.mySlice= results;
        this.hasNext=b;
    }
}
