package com.example.just.Dao;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHashTagMap is a Querydsl query type for HashTagMap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHashTagMap extends EntityPathBase<HashTagMap> {

    private static final long serialVersionUID = -1800252931L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHashTagMap hashTagMap = new QHashTagMap("hashTagMap");

    public final QHashTag hashTag;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPost post;

    public QHashTagMap(String variable) {
        this(HashTagMap.class, forVariable(variable), INITS);
    }

    public QHashTagMap(Path<? extends HashTagMap> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHashTagMap(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHashTagMap(PathMetadata metadata, PathInits inits) {
        this(HashTagMap.class, metadata, inits);
    }

    public QHashTagMap(Class<? extends HashTagMap> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hashTag = inits.isInitialized("hashTag") ? new QHashTag(forProperty("hashTag")) : null;
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
    }

}

