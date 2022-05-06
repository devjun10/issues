package com.example.querydsl.controller.dto;

import lombok.Data;

@Data
public class MemberSearchCondition {
    private String memberName;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}
