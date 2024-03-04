package com.example.just.Response;

import com.example.just.Dao.Blame;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseBlameList {

    private int blameCount;

    private List<ResponseBlameDto> blameList;

}
