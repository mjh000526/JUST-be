package com.example.just.Dao;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Enumerated(EnumType.STRING)
    @Setter
    private Role role;

    @CreationTimestamp
    private Timestamp createTime;

    private String provider;

    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "post_like",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private List<Post> likedPosts = new ArrayList<>();

    public void addLikedPost(Post post) {
        if (!likedPosts.contains(post)) {
            likedPosts.add(post);
            post.addLike(this);
        }
    }

    public void removeLikedPost(Post post) {
        if (likedPosts.contains(post)) {
            likedPosts.remove(post);
            post.removeLike(this);
        }
    }



    public void updateMember(final Post post) {
        posts.add(post);
    }
}

