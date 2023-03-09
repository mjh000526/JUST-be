package com.example.just.Dao;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_email")
    private String member_email;    //회원 이메일

    @Column(name="member_register")
    private Date member_register;   //회원 등록일

    @Column(name = "password")
    private String password;

    @Column(name = "provider")
    private String provider;

    @Column(name = "username")
    private String username;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    public void updateMember(final Post post) {
        posts.add(post);
    }

}
