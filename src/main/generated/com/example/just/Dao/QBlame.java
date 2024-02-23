package com.example.just.Dao;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBlame is a Querydsl query type for Blame
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlame extends EntityPathBase<Blame> {

    private static final long serialVersionUID = 2136504610L;

    public static final QBlame blame = new QBlame("blame");

    public final DateTimePath<java.util.Date> blameDatetime = createDateTime("blameDatetime", java.util.Date.class);

    public final NumberPath<Long> blameId = createNumber("blameId", Long.class);

    public final NumberPath<Long> blameMemberId = createNumber("blameMemberId", Long.class);

    public final NumberPath<Long> target_Index = createNumber("target_Index", Long.class);

    public final NumberPath<Long> targetCommentId = createNumber("targetCommentId", Long.class);

    public final NumberPath<Long> targetMemberId = createNumber("targetMemberId", Long.class);

    public final NumberPath<Long> targetPostId = createNumber("targetPostId", Long.class);

    public QBlame(String variable) {
        super(Blame.class, forVariable(variable));
    }

    public QBlame(Path<? extends Blame> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBlame(PathMetadata metadata) {
        super(Blame.class, metadata);
    }

}

