package com.example.querydsl.repository;

import com.example.querydsl.domain.member.Member;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.example.querydsl.domain.member.QMember.member;
import static com.example.querydsl.domain.team.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

    /**
     * 트랜잭션 단위로 모두 따로 분리하게 된다.
     * 프록시를 주입해준다. 다른 곳에 바인딩되도록 해준다.
     * 따라서 동시성 문제가 발생하지 않는다.
     */
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public void save(Member member) {
        entityManager.persist(member);
    }

    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Member.class, id));
    }

    public List<Member> findAll() {
        return entityManager.createQuery("SELECT m FROM Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByMembername(String memberName) {
        return entityManager.createQuery("SELECT m FROM Member m WHERE m.memberName = :memberName", Member.class)
                .setParameter("memberName", memberName)
                .getResultList();
    }

    public List<Member> findAllQueryDsl() {
        return queryFactory.selectFrom(member)
                .fetch();
    }

    public List<Member> findAllByNameQueryDsl(String name) {
        return queryFactory.selectFrom(member)
                .where(member.memberName.eq(name))
                .fetch();
    }

    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {
        return queryFactory.select(
                        new QMemberTeamDto(
                                member.memberId.as("memberId"),
                                member.memberName,
                                member.age,
                                team.teamId.as("teamId"),
                                team.teamName
                        ))
                .from(member)
                .leftJoin(member.team, team)
                .fetch();
    }

    public List<MemberTeamDto> search(MemberSearchCondition condition) {
        return queryFactory.select(
                        new QMemberTeamDto(
                                member.memberId.as("memberId"),
                                member.memberName,
                                member.age,
                                team.teamId.as("teamId"),
                                team.teamName
                        ))
                .from(member)
                .where(
                        memberNameEq(condition.getMemberName()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .leftJoin(member.team, team)
                .fetch();
    }

    private BooleanExpression between(int ageLoe, int ageGoe) {
        return ageGoe(ageLoe).and(ageGoe(ageGoe));
    }

    private BooleanExpression memberNameEq(String memberName) {
        return hasText(memberName) ? member.memberName.eq(memberName) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return hasText(teamName) ? team.teamName.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }
}
