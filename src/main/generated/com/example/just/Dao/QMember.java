package com.example.just.Dao;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 2115936519L;

    public static final QMember member = new QMember("member1");

    public final EnumPath<Role> authority = createEnum("authority", Role.class);

    public final NumberPath<Integer> blameCount = createNumber("blameCount", Integer.class);

    public final NumberPath<Integer> blamedCount = createNumber("blamedCount", Integer.class);

    public final DateTimePath<java.sql.Timestamp> createTime = createDateTime("createTime", java.sql.Timestamp.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Comment, QComment> likedComments = this.<Comment, QComment>createList("likedComments", Comment.class, QComment.class, PathInits.DIRECT2);

    public final ListPath<Post, QPost> likedPosts = this.<Post, QPost>createList("likedPosts", Post.class, QPost.class, PathInits.DIRECT2);

    public final StringPath nickname = createString("nickname");

    public final ListPath<Notification, QNotification> notifications = this.<Notification, QNotification>createList("notifications", Notification.class, QNotification.class, PathInits.DIRECT2);

    public final ListPath<Post, QPost> posts = this.<Post, QPost>createList("posts", Post.class, QPost.class, PathInits.DIRECT2);

    public final StringPath provider = createString("provider");

    public final StringPath provider_id = createString("provider_id");

    public final StringPath refreshToken = createString("refreshToken");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

