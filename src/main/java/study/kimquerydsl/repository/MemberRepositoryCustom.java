package study.kimquerydsl.repository;

import java.util.List;
import study.kimquerydsl.dto.MemberSearchCondition;
import study.kimquerydsl.dto.MemberTeamDto;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSearchCondition condition);
}