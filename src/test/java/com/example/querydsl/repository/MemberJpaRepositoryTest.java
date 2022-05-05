package com.example.querydsl.repository;

import com.example.querydsl.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    MemberJpaRepository memberJpaRepository;


    @Test
    @DisplayName("")
    void m() throws Exception {
        Member member = new Member("memberA", 10);
        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.findById(member.getMemberId()).get();
        assertThat(findMember).isEqualTo(member);

        List<Member> members = memberJpaRepository.findAll();
        assertThat(members).contains(member);

        List<Member> findMembersByName = memberJpaRepository.findByMembername("memberA");
        assertThat(findMembersByName).contains(member);
    }

    @Test
    @DisplayName("")
    void m2() throws Exception {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        Member memberC = new Member("memberC", 30);
        Member memberD = new Member("memberD", 40);

        entityManager.persist(memberA);
        entityManager.persist(memberB);
        entityManager.persist(memberC);
        entityManager.persist(memberD);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(29);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberJpaRepository.search(condition);

        assertThat(result.size()).isEqualTo(1);

    }
}
