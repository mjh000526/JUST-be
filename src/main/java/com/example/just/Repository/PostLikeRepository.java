package com.example.just.Repository;

import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dao.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    PostLike findByPostAndMember(Post post, Member member);
}
