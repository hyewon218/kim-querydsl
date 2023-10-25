package study.kimquerydsl;

import static org.assertj.core.api.Assertions.*;
import static study.kimquerydsl.entity.QMember.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.kimquerydsl.entity.Member;
import study.kimquerydsl.entity.Team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory; // JPAQueryFactory 를 필드로

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    // JPQL: 문자(실행 시점 오류 발견)
    public void startJPQL() {
        // member1을 찾아라.
        String qlString =
            "select m from Member m " +
                "where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
            .setParameter("username", "member1")// 파라미터 바인딩 직접 처리
            .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    // Querydsl: 코드(컴파일 시점 오류 발견)
    public void startQuerydsl() {
        // member1을 찾아라.

        Member findMember = queryFactory
            .select(member)
            .from(member)
            .where(member.username.eq("member1"))// 파라미터 바인딩 자동 처리
            .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    // 기본 검색 쿼리
    public void search() {
        Member findMember = queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1")
                .and(member.age.between(10, 30)))
            .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    // AND 조건을 파라미터로 처리
    public void searchAndParam() {
        List<Member> result1 = queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1"),
                member.age.eq(10))
            .fetch();
        assertThat(result1.size()).isEqualTo(1);
    }
}