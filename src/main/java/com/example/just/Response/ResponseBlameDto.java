package com.example.just.Response;

import com.example.just.Dao.Blame;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBlameDto {
    private Long blame_id;

    private Long target_index; //신고내용 index

    private Date blame_date_time; //신고를한 일시

    private Long blame_member_id; //신고를 한 회원

    private Long target_member_id; //신고를 당한 회원

    private Long target_post_id; //신고를 한 회원

    private Long target_comment_id; //신고를 당한 회원

    private String message; //결과 메세지

    public ResponseBlameDto(Blame blame,String message){
        blame_id = blame == null ? null : blame.getBlameId();
        target_index = blame == null ? null : blame.getTargetIndex();
        blame_date_time = blame == null ? null : blame.getBlameDatetime();
        blame_member_id = blame == null ? null : blame.getBlameMemberId();
        target_member_id = blame == null ? null : blame.getTargetMemberId();
        target_post_id = blame == null ? null : blame.getTargetPostId();
        target_comment_id = blame == null ? null : blame.getTargetCommentId();
        this.message = message;
    }

}
