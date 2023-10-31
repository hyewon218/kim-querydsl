package study.kimquerydsl.repository;

import static org.springframework.util.StringUtils.hasText;
import static study.kimquerydsl.entity.QMember.member;
import static study.kimquerydsl.entity.QTeam.team;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import study.kimquerydsl.dto.MemberSearchCondition;
import study.kimquerydsl.dto.MemberTeamDto;
import study.kimquerydsl.dto.QMemberTeamDto;
import study.kimquerydsl.entity.Member;

@Repository
//@RequiredArgsConstructor
public class MemberJpaRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public MemberJpaRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public void save(Member member) {
        em.persist(member);
    }

    public Optional<Member> findById(Long id) {
        Member findMember = em.find(Member.class, id);
        return Optional.ofNullable(findMember);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
            .getResultList();
    }

    public List<Member> findAll_Querydsl() {
        return queryFactory
            .selectFrom(member)
            .fetch();
    }

    public List<Member> findByUsername(String username) {
        return em.createQuery("select m from Member m where m.username = :username ", Member.class)
            .setParameter("username", username)
            .getResultList();
    }

    public List<Member> findByUsername_Querydsl(String username) {
        return queryFactory
            .selectFrom(member)
            .where(member.username.eq(username))
            .fetch();
    }

    // Builder 사용
    // 회원명, 팀명, 나이(ageGoe, ageLoe)
    public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {

        BooleanBuilder builder = new BooleanBuilder();

        if (hasText(condition.getUsername())) {
            builder.and(member.username.eq(condition.getUsername()));
        }
        if (hasText(condition.getTeamName())) {
            builder.and(team.name.eq(condition.getTeamName()));
        }
        if (condition.getAgeGoe() != null) {
            builder.and(member.age.goe(condition.getAgeGoe()));
        }
        if (condition.getAgeLoe() != null) {
            builder.and(member.age.loe(condition.getAgeLoe()));
        }
        return queryFactory
            .select(new QMemberTeamDto(
                member.id,
                member.username,
                member.age,
                team.id,
                team.name))
            .from(member)
            .leftJoin(member.team, team)
            .where(builder)
            .fetch();
    }
}