package com.example.querydsl.domain.team;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeam is a Querydsl query type for Team
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeam extends EntityPathBase<Team> {

    private static final long serialVersionUID = -1585428756L;

    public static final QTeam team = new QTeam("team");

    public final ListPath<com.example.querydsl.domain.member.Member, com.example.querydsl.domain.member.QMember> members = this.<com.example.querydsl.domain.member.Member, com.example.querydsl.domain.member.QMember>createList("members", com.example.querydsl.domain.member.Member.class, com.example.querydsl.domain.member.QMember.class, PathInits.DIRECT2);

    public final NumberPath<Long> teamId = createNumber("teamId", Long.class);

    public final StringPath teamName = createString("teamName");

    public QTeam(String variable) {
        super(Team.class, forVariable(variable));
    }

    public QTeam(Path<? extends Team> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTeam(PathMetadata metadata) {
        super(Team.class, metadata);
    }

}

