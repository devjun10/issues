package com.example.querydsl.repository;

import lombok.Data;

@Data
public class MemberSearchCondition {
    private String memberName;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}
