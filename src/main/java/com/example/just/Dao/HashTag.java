package com.example.just.Dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
public class HashTag { //DB 태그 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hash_tag_id")
    private Long id; //태그id

    @Column(name = "name")
    private String name; //태그명

    @Column(name = "tag_count")
    private Long tagCount;//태그가 사용된 횟수

    @OneToMany(mappedBy = "hashTag", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<HashTagMap> hashTagMaps = new ArrayList<>();//게시글과 N:M


    public HashTag() {
    }

    public HashTag(String name) {
        this.name = name;
    }

    public void addHashTagMap(HashTagMap hashTagMap) {
        this.hashTagMaps.add(hashTagMap);
    }
}