package com.example.querydsl.repository;

import com.example.querydsl.controller.dto.MemberSearchCondition;
import com.example.querydsl.controller.dto.MemberTeamDto;
import com.example.querydsl.domain.member.Member;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.example.querydsl.domain.member.QMember.member;
import static com.example.querydsl.domain.team.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

public class MemberRepositoryImpl extends QuerydslRepositorySupport implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Member.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public List<MemberTeamDto> search(MemberSearchCondition condition) {

        from(member)
                .leftJoin(member.team, team)
                .where(
                        memberNameEq(condition.getMemberName()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .fetch();


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

    /**
     * 페이지가 0이면 offset이 발동되지 않는다.
     * orderBy가 들어갈 경우 제외해준다. 이는 갯수와는 아무런 상관이 없기 때문이다.
     */
    @Override
    public Page<MemberTeamDto> searchSimple(MemberSearchCondition condition, Pageable pageable) {
        // pageresult를 사용하면 count 쿼리르 추가로 날려준다.
        QueryResults<MemberTeamDto> result = queryFactory.select(
                        new QMemberTeamDto(
                                member.memberId.as("memberId"),
                                member.memberName,
                                member.age,
                                team.teamId.as("teamId"),
                                team.teamName
                        ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        memberNameEq(condition.getMemberName()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<MemberTeamDto> content = result.getResults();
        long total = result.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * QueryDslSupport가 지원하는 Paging 쿼리
     */
    public Page<MemberTeamDto> searchSimple2(MemberSearchCondition condition, Pageable pageable) {
        // pageresult를 사용하면 count 쿼리르 추가로 날려준다.
        JPQLQuery<MemberTeamDto> jpaQuery = from(member)
                .leftJoin(member.team, team)
                .where(
                        memberNameEq(condition.getMemberName()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .select(new QMemberTeamDto(
                        member.memberId.as("memberId"),
                        member.memberName,
                        member.age,
                        team.teamId.as("teamId"),
                        team.teamName.as("teamName")
                ));

        JPQLQuery<MemberTeamDto> query = getQuerydsl().applyPagination(pageable, jpaQuery);

        List<MemberTeamDto> content = query.fetch();
        return new PageImpl<>(content, pageable, 3l);
    }

    /**
     * 상황에 따라 다른데, 조인이 필요하지 않을 필요가 없다.
     * content는 복잡하지만, total은 간단하게 구할 수 있을 때.
     * fetchResult는 최적화를 하지 못한다.
     * 이를 최적화하고 싶다면 별도로 쪼개서 완전히 분리해내야 한다.
     * <p>
     * count쿼리를 실행하고 count 가없으면 fetch를 하지 않는다는 등의
     * 최적화를 할 수 있다.
     * <p>
     * 이를 위해서는 최적화해야 한다.
     */
    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory.select(
                        new QMemberTeamDto(
                                member.memberId.as("memberId"),
                                member.memberName,
                                member.age,
                                team.teamId.as("teamId"),
                                team.teamName
                        ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        memberNameEq(condition.getMemberName()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(
                        memberNameEq(condition.getMemberName()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    public Page<MemberTeamDto> searchPag3eComplex(MemberSearchCondition condition, Pageable pageable) {
        List<MemberTeamDto> content = queryFactory.select(
                        new QMemberTeamDto(
                                member.memberId.as("memberId"),
                                member.memberName,
                                member.age,
                                team.teamId.as("teamId"),
                                team.teamName
                        ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        memberNameEq(condition.getMemberName()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Member> countQuery = queryFactory
                .selectFrom(member)
                .leftJoin(member.team, team)
                .where(
                        memberNameEq(condition.getMemberName()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                );
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    /**
     * count 쿼리가 생략가능할 때 -
     * - 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
     * - 마지막 페이지일 때(offset,+ 컨텐츠 사이즈를 더해서 전체 사이즈를 구한다)
     *
     * */
}
