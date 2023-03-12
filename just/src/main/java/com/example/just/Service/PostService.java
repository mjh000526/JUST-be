package com.example.just.Service;


import com.example.just.Dao.Member;
import com.example.just.Dao.Post;
import com.example.just.Dao.QPost;
import com.example.just.Dto.PostDto;
import com.example.just.Impl.MySliceImpl;
import com.example.just.Repository.MemberRepository;
import com.example.just.Repository.PostRepository;


import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;


import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class PostService {


    private final EntityManager em;

    private final JPAQueryFactory query;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    public PostService(EntityManager em, JPAQueryFactory query) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }



    public Post write(Long member_id, PostDto postDto) {


        Member member = memberRepository.findById(member_id).orElseGet(Member::new);
        Post post = new Post(postDto.getPost_content(),
                postDto.getPost_tag(), postDto.getPost_like(),
                postDto.getPost_create_time(), postDto.isSecret(),
                postDto.getEmoticon(), postDto.getPost_category(),
                member);

        postRepository.save(post);




       return post;
    }

    public Slice<Post> searchByCursor(String cursor, Long limit) {


        QPost post = QPost.post;
        Set<Long> viewedPostIds = new HashSet<>();

        // 이전에 본 글들의 ID를 가져옵니다.
        if (cursor != null) {
            String[] viewedPostIdsArray = cursor.split(",");
            viewedPostIds = new HashSet<>();
            for (String viewedPostId : viewedPostIdsArray) {
                viewedPostIds.add(Long.parseLong(viewedPostId));
            }
        }

        // 중복된 글을 제외하고 랜덤으로 limit+1개의 글을 가져옵니다.
        List<Post> results = query.selectFrom(post)
                .where(post.post_id.notIn(viewedPostIds),
                        post.post_create_time.isNotNull())
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(limit + 1)
                .fetch();

        // 가져온 글들의 ID를 저장합니다.
        Set<Long> resultPostIds = results.stream().map(Post::getPost_id).collect(Collectors.toSet());
        viewedPostIds.addAll(resultPostIds);

        // hasNext와 nextCursor를 계산합니다.
        boolean hasNext = results.size() > limit;
        String nextCursor = null;
        if (!viewedPostIds.isEmpty()) {
            nextCursor = String.join(",", viewedPostIds.stream().map(Object::toString).collect(Collectors.toList()));
        }

        // limit+1개의 글 중에서 limit개의 글만 남기고 제거합니다.
        if (hasNext) {
            results.remove(limit);
        }
        System.out.println("Slice.ofElem"+results.size());
        // Slice 객체를 생성해서 반환합니다.
        return new MySliceImpl<>(results, PageRequest.of(0, Math.toIntExact(limit)), hasNext, nextCursor);

    }


}
