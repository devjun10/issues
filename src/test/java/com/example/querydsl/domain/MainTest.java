package com.example.querydsl.domain;

import com.example.querydsl.domain.member.Member;
import com.example.querydsl.domain.member.QMember;
import com.example.querydsl.domain.team.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.List;

import static com.example.querydsl.domain.member.QMember.member;
import static com.example.querydsl.domain.team.QTeam.team;
import static com.querydsl.jpa.JPAExpressions.select;

@SpringBootTest
@Transactional
@Commit
class MainTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Test
    // @Before
    @DisplayName("데이터 초기화")
    void beforeTest() {
        entityManager.persist(new Member("Kim", 31, new Team("TeamB")));
        entityManager.persist(new Member("Jang", 42, new Team("TeamC")));
        entityManager.persist(new Member("Jerry", 17, new Team("TeamA")));
        entityManager.persist(new Member("Hoo", 26, new Team("TeamD")));
        entityManager.persist(new Member("Po", 30, new Team("TeamF")));
    }

    @Test
    @DisplayName("영속성 저장")
    void save() throws Exception {
        entityManager.persist(new Member("Jun", 30, new Team("TeamA")));
        List<Member> members = jpaQueryFactory
                .selectFrom(member)
                .where(member.age.eq(30))
                .fetch();

        Assertions.assertThat(members.size()).isEqualTo(1);
    }

    /**
     * ,로 조건을 나열할 경우 null 값이 들어왔을 때 무시할 수 있다.
     */
    @Test
    @DisplayName("검색")
    void search() throws Exception {
        Member findMember = jpaQueryFactory.selectFrom(member)
                .where(member.age.eq(30),
                        member.memberName.eq("Jun"))
                .fetchOne();

        Assertions.assertThat(findMember.getMemberName()).isEqualTo("Jun");
        Assertions.assertThat(findMember.getAge()).isEqualTo(30);
    }


    /**
     * 결과조회.
     * FetchOne의 결과가 둘 이상이면 NonUniqueResultException이 발생한다.
     */
    @Test
    @DisplayName("FetchFirst")
    void resultFetchFirst() throws Exception {
        Member findMember = jpaQueryFactory.selectFrom(member)
                .fetchFirst();

        Member findMemberB = jpaQueryFactory.selectFrom(member)
                .limit(1)
                .fetchOne();

        Assertions.assertThat(findMember.getMemberName()).isEqualTo(findMemberB.getMemberName());
    }

    /**
     * 전체 데이터 쿼리와 전체 갯수가 다를 수 있기 때문에 이 경우 최적화를 해야 한다.
     */
    @Test
    @DisplayName("FetchResults")
    void fetchResultsV1() throws Exception {
        QueryResults<Member> members = jpaQueryFactory.selectFrom(member)
                .fetchResults();

        long totalCount = members.getTotal();
        List<Member> memberList = members.getResults();

        Assertions.assertThat(memberList.size()).isEqualTo(totalCount);
    }

    /**
     * 카운트 전용 쿼리
     */
    @Test
    @DisplayName("FetchResults_V2")
    void fetchResultsV2() throws Exception {
        QueryResults<Member> members = jpaQueryFactory
                .selectFrom(member)
                .fetchResults();

        long totalCount = members.getTotal();
        long total = jpaQueryFactory.selectFrom(member).fetchCount();

        Assertions.assertThat(total).isEqualTo(totalCount);
    }

    /**
     * 회원 정렬
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 2단계에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    @DisplayName("정렬")
    void sort() throws Exception {
        List<Member> result = jpaQueryFactory.selectFrom(member)
                .where(member.age.lt(35))
                .orderBy(member.age.desc(), member.memberName.asc().nullsFirst())
                .fetch();

        result.forEach(System.out::println);
    }

    @Test
    @DisplayName("페이징 V1")
    void paging1() throws Exception {
        List<Member> result = jpaQueryFactory.selectFrom(member)
                .orderBy(member.memberName.desc())
                .offset(1)
                .limit(3)
                .fetch();

        Assertions.assertThat(result.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("페이징 V2")
    void paging2() throws Exception {
        QueryResults<Member> result = jpaQueryFactory.selectFrom(member)
                .orderBy(member.memberName.desc())
                .offset(1)
                .limit(3)
                .fetchResults();

        Assertions.assertThat(result.getTotal()).isEqualTo(6);
        Assertions.assertThat(result.getOffset()).isEqualTo(1);
        Assertions.assertThat(result.getLimit()).isEqualTo(3);
    }

    @Test
    @DisplayName("집합")
    void aggregation() throws Exception {
        List<Tuple> result = jpaQueryFactory.select(
                        member.count(), member.age.sum(), member.age.avg()
                ).from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("Tuple= " + tuple);
        }
    }

    @Test
    @DisplayName("일반조인 V1")
    void joinV1() throws Exception {
        List<Member> result = jpaQueryFactory.selectFrom(member)
                .join(member.team, team)
                .where(team.teamName.eq("teamA"))
                .fetch();

        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("세타조인 V2")
    void joinV2() throws Exception {
        List<Member> result = jpaQueryFactory.select(member)
                .from(member, team)
                .where(member.team.teamName.eq("teamA"))
                .fetch();

        Assertions.assertThat(result.size()).isEqualTo(12);
    }

    @Test
    @DisplayName("조인 필터링 - Left Join")
    void joinV3() throws Exception {
        List<Tuple> result = jpaQueryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.teamName.eq("teamA"))
                .fetch();
    }

    @Test
    @DisplayName("카디널리티 곱")
    void 카디널리티곱() throws Exception {
        List<Tuple> result = jpaQueryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.memberName.eq(team.teamName))
                .fetch();

        List<Tuple> resultB = jpaQueryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(member.memberName.eq(team.teamName))
                .fetch();

        Assertions.assertThat(result.size()).isEqualTo(resultB.size());
    }

    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("isLoaded")
    void isLoaded() throws Exception {
        Member findMember = jpaQueryFactory.selectFrom(member)
                .join(member.team, team)
                .fetchJoin()
                .where(member.memberName.eq("Jun"))
                .fetchOne();

        boolean loaded = entityManagerFactory
                .getPersistenceUnitUtil().isLoaded(findMember.getTeam());

        Assertions.assertThat(loaded).isTrue();
    }

    @Test
    @DisplayName("서브쿼리 Where절")
    void 서브쿼리_Max() throws Exception {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = jpaQueryFactory.selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max())
                                .from(memberSub)
                )).fetch();

        Assertions.assertThat(result.get(0).getAge()).isEqualTo(42);
    }

    @Test
    @DisplayName("서브쿼리 Where절 Max")
    void 서브쿼리() throws Exception {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = jpaQueryFactory.selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                )).fetch();

        Assertions.assertThat(result.size()).isLessThan(6);
    }

    @Test
    @DisplayName("서브쿼리 in절")
    void 서브쿼리_in절() throws Exception {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = jpaQueryFactory.selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                )).fetch();

        Assertions.assertThat(result).extracting("age")
                .contains(42);
    }

    /**
     * From절의 서브 쿼리(인라인 뷰)가 안된다.
     * 원래는 select절의 서브쿼리도 지원하지 않는다.
     * 1. 서브쿼리는 조인으로 바꿀 수 있기 때문에 이 경우 .
     * 2. 쿼리를 분리한다.
     * 3. NativeSQL을 사용해야 한다.
     */
    @Test
    @DisplayName("Where절 서브쿼리 V4")
    void subquery_in절4() throws Exception {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = jpaQueryFactory.selectFrom(member)
                .where(member.age.in(select(memberSub.age)
                        .from(memberSub)
                        .where(memberSub.age.gt(10))))
                .fetch();
    }

    @Test
    @DisplayName("간단한 Case문")
    void 간단한_Case문() throws Exception {
        List<String> result = jpaQueryFactory.select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
    }

    /**
     * 데이터베이스에서는 최소한의 조건으로 데이터를 가져온 후
     * 애플리케이션 로직으로 풀어가는 것이 좋다.
     */
    @Test
    @DisplayName("복잡한 Case문")
    void 복잡한_Case문() throws Exception {
        List<String> result = jpaQueryFactory.select(
                        new CaseBuilder()
                                .when(member.age.between(0, 20)).then("0~20살")
                                .when(member.age.between(21, 30)).then("21~30살")
                                .otherwise("기타"))
                .from(member)
                .fetch();
    }

    @Test
    @DisplayName("Concat_V1")
    void 상수더하기V1() throws Exception {
        List<Tuple> result = jpaQueryFactory
                .select(member.memberName, member.age, Expressions.constant("A"))
                .from(member)
                .fetch();
        result.forEach(System.out::println);
    }

    /**
     * string.value()는 Enum 문자열 더하기 등에서 자주 사용된다.
     */
    @Test
    @DisplayName("Concat_V2")
    void 상수더하기V2() throws Exception {
        List<String> result = jpaQueryFactory
                .select(
                        member.memberName.concat(", ").concat(member.age.stringValue())
                )
                .from(member)
                .where(member.memberName.eq("Jun"))
                .fetch();

        result.forEach(System.out::println);
    }
}
