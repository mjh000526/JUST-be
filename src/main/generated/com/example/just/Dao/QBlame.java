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

    public final DateTimePath<java.util.Date> blame_datetime = createDateTime("blame_datetime", java.util.Date.class);

    public final NumberPath<Long> blame_id = createNumber("blame_id", Long.class);

    public final NumberPath<Long> blame_member_id = createNumber("blame_member_id", Long.class);

    public final NumberPath<Long> target_id = createNumber("target_id", Long.class);

    public final NumberPath<Long> target_member_id = createNumber("target_member_id", Long.class);

    public final NumberPath<Integer> target_type = createNumber("target_type", Integer.class);

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

