package study.kimquerydsl.dto;

import lombok.Data;

@Data
public class MemberSearchCondition {//회원 검색 조건

    //회원명, 팀명, 나이(ageGoe, ageLoe)
    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}