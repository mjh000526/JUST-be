package com.example.just.Dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HashTagMap { //DB 글과 태그와의 N:M을 위한 매핑테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;//게시글id

    @ManyToOne
    @JoinColumn(name = "hash_tag_id")
    @JsonIgnore
    private HashTag hashTag;//태그id

    public HashTagMap(HashTag newHashTag, Post p) {
        this.hashTag = newHashTag;
        this.post = p;
    }
}
