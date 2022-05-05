package com.example.querydsl.repository;

import com.querydsl.core.annotations.QueryProjection;

public class MemberTeamDto {

    private Long memberId;
    private String name;
    private int age;
    private Long teamId;
    private String teamName;

    @QueryProjection
    public MemberTeamDto(Long memberId, String name, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.name = name;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
