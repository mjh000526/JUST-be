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
public class ResponseBlameList {//클라이언트에 응답할 신고 리스트 포맷

    private int blameCount;

    private List<ResponseBlameDto> blameList;

}
