package com.example.just.Dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hash_tag_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "tag_count")
    private Long tagCount;

    @ManyToMany()
    @JoinTable(
            name = "hash_tag_map",
            joinColumns = @JoinColumn(name = "hash_tag_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    @JsonIgnore
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

}