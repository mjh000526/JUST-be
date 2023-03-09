package com.example.just.Dao;

import javax.persistence.*;

@Entity
@Table(name ="category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long category_id; //카테고리 아이디

    @Column(name = "love")
    private String love;

    @Column(name = "company")
    private String company;

    @Column(name = "school")
    private String school;

}
