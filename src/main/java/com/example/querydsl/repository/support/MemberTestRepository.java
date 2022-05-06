package com.example.querydsl.repository.support;

import com.example.querydsl.domain.member.Member;
import com.example.querydsl.controller.dto.MemberSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.querydsl.domain.member.QMember.member;
import static com.example.querydsl.domain.team.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class MemberTestRepository extends Querydsl4RepositorySupport {

    public MemberTestRepository() {
        super(Member.class);
    }


    public List<Member> basicSelect() {
        return select(member)
                .from(member)
                .fetch();
    }

    public List<Member> basicSelectFrom() {
        return selectFrom(member)
                .fetch();

    }


    public Page<Member> searchPageByApplyPage(MemberSearchCondition condition, Pageable pageable) {
        JPAQuery<Member> query = selectFrom(member)
                .leftJoin(member.team, team)
                .where(
                        memberNameEq(condition.getMemberName()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );

        List<Member> content = getQuerydsl().applyPagination(pageable, query).fetch();

        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }

    public Page<Member> applyPagination(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable,
                query ->
                        query.selectFrom(member)
                                .leftJoin(member.team, team)
                                .where(
                                        memberNameEq(condition.getMemberName()),
                                        teamNameEq(condition.getTeamName()),
                                        ageGoe(condition.getAgeGoe()),
                                        ageLoe(condition.getAgeLoe())
                                ));
    }


    /**
     * count 쿼리 분리
     */
    public Page<Member> applyPagination2(MemberSearchCondition condition, Pageable pageable) {
        return applyPagination(pageable,
                contentQuery ->
                        contentQuery.selectFrom(member)
                                .leftJoin(member.team, team)
                                .where(
                                        memberNameEq(condition.getMemberName()),
                                        teamNameEq(condition.getTeamName()),
                                        ageGoe(condition.getAgeGoe()),
                                        ageLoe(condition.getAgeLoe())
                                ), countQuery -> countQuery.select(
                                member.memberId)
                        .from(member)
                        .leftJoin(member.team, team)
                        .where(
                                memberNameEq(condition.getMemberName()),
                                teamNameEq(condition.getTeamName()),
                                ageGoe(condition.getAgeGoe()),
                                ageLoe(condition.getAgeLoe())
                        )
        );
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
