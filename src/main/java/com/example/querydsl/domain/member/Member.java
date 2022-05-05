package com.example.querydsl.domain.member;

import com.example.querydsl.domain.team.Team;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(name = "name")
    private String memberName;

    @Column(name = "age")
    private int age;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String memberName) {
        this(memberName, 0);
    }

    public Member(String memberName, int age) {
        this(memberName, age, null);
    }

    public Member(String memberName, int age, Team team) {
        this.memberName = memberName;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public Team getTeam() {
        return team;
    }

    private void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", memberId)
                .append("name", memberName)
                .append("age", age)
                .append("team", team)
                .toString();
    }
}
