package com.example.just.Dao;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPostContent is a Querydsl query type for PostContent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPostContent extends EntityPathBase<PostContent> {

    private static final long serialVersionUID = 2019435884L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPostContent postContent = new QPostContent("postContent");

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPost post;

    public QPostContent(String variable) {
        this(PostContent.class, forVariable(variable), INITS);
    }

    public QPostContent(Path<? extends PostContent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPostContent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPostContent(PathMetadata metadata, PathInits inits) {
        this(PostContent.class, metadata, inits);
    }

    public QPostContent(Class<? extends PostContent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
    }

}

