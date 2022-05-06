package com.example.querydsl.config;//package com.example.dailyapp.config;

import com.example.querydsl.domain.member.Member;
import com.example.querydsl.domain.team.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

//@Component
@RequiredArgsConstructor
public class InitDatabase {

    private final InitService initService;
    private List<String> teamNames = new ArrayList<>();

    // Transaction과 분리해줘야 한다.
    @PostConstruct
    public void init() {
        addTeamName();
        initService.memberInit();
    }

    private void addTeamName() {
        teamNames.add("teamA");
        teamNames.add("teamB");
        teamNames.add("teamC");
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        @PersistenceContext
        private EntityManager entityManager;


        @Transactional
        public void memberInit() {
            Team teamA = new Team("teamA");
            Team teamB = new Team("teamB");

            entityManager.persist(teamA);
            entityManager.persist(teamB);

            for (int index = 0; index < 100; index++) {
                Team selectedTeam = index % 2 == 0 ? teamA : teamB;
                entityManager.persist(new Member("member" + index, index, selectedTeam));
            }
        }

        private void getPersist(Member member) {
            entityManager.persist(member);
        }

        private Member getMember(String name, int age, Team team) {
            return new Member(name, age, team);
        }
    }
}
