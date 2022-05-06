package com.example.querydsl.repository;

import com.example.querydsl.controller.dto.MemberSearchCondition;
import com.example.querydsl.controller.dto.MemberTeamDto;
import com.example.querydsl.domain.member.Member;
import com.example.querydsl.domain.team.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    //
    @Autowired
    MemberRepository memberRepository;


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

    @Test
    @DisplayName("")
    void m3() throws Exception {
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

        List<MemberTeamDto> result = memberRepository.search(condition);

        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("")
    void searchPageSimple() throws Exception {

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        entityManager.persist(teamA);
        entityManager.persist(teamB);

        Member memberA = new Member("memberA", 10, teamA);
        Member memberB = new Member("memberB", 20, teamB);
        Member memberC = new Member("memberC", 30, teamA);
        Member memberD = new Member("memberD", 40, teamB);

        entityManager.persist(memberA);
        entityManager.persist(memberB);
        entityManager.persist(memberC);
        entityManager.persist(memberD);

        MemberSearchCondition condition = new MemberSearchCondition();

        PageRequest pageRequest = PageRequest.of(0, 3);
        Page<MemberTeamDto> result = memberRepository.searchSimple(condition, pageRequest);


        assertThat(result.getSize()).isEqualTo(3);
//        assertThat(result.getContent()).contains();

    }
}
