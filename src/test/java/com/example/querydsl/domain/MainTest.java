package com.example.querydsl.domain;

import com.example.querydsl.domain.member.Member;
import com.example.querydsl.domain.team.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.querydsl.domain.member.QMember.member;

@SpringBootTest
@Transactional
@Commit
class MainTest {

    @Autowired
    EntityManager entityManager;

    @Autowired JPAQueryFactory jpaQueryFactory;

    @Test
    // @Before
    @DisplayName("데이터 초기화")
    void beforeTest(){
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
        List<Member> members = jpaQueryFactory.selectFrom(member)
                .where(member.age.eq(30))
                .fetch();

        Assertions.assertThat(members.size()).isEqualTo(1);
    }

    /**
     *  ,로 조건을 나열할 경우 null 값이 들어왔을 때 무시할 수 있다.
     * */
    @Test
    @DisplayName("검색")
    void search() throws Exception {
        Member findMember = jpaQueryFactory.selectFrom(member)
                        .where(member.age.eq(30),
                                member.name.eq("Jun"))
                                .fetchOne();

        Assertions.assertThat(findMember.getName()).isEqualTo("Jun");
        Assertions.assertThat(findMember.getAge()).isEqualTo(30);
    }


    /**
     * 결과조회.
     * FetchOne의 결과가 둘 이상이면 NonUniqueResultException이 발생한다.
     * */
    @Test
    @DisplayName("FetchFirst")
    void resultFetchFirst() throws Exception {
        Member findMember = jpaQueryFactory.selectFrom(member)
                .fetchFirst();

        Member findMemberB = jpaQueryFactory.selectFrom(member)
                .limit(1)
                .fetchOne();

        Assertions.assertThat(findMember.getName()).isEqualTo(findMemberB.getName());
    }

    /**
     * 전체 데이터 쿼리와 전체 갯수가 다를 수 있기 때문에 이 경우 최적화를 해야 한다.
     * */
    @Test
    @DisplayName("FetchResults")
    void fetchResults() throws Exception {
        QueryResults<Member> members = jpaQueryFactory.selectFrom(member)
                .fetchResults();

        long totalCount = members.getTotal();
        List<Member> memberList = members.getResults();

        Assertions.assertThat(memberList.size()).isEqualTo(totalCount);
    }

    /**
     * 카운트 전용 쿼리
     * */
    @Test
    @DisplayName("FetchResults_V2")
    void fetchResultsV2() throws Exception {
        QueryResults<Member> members = jpaQueryFactory.selectFrom(member)
                .fetchResults();

        long totalCount = members.getTotal();
        long total = jpaQueryFactory.selectFrom(member).fetchCount();

        Assertions.assertThat(total).isEqualTo(totalCount);
    }




}