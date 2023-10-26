package study.kimquerydsl;

import static org.assertj.core.api.Assertions.*;
import static study.kimquerydsl.entity.QMember.*;

import com.querydsl.core.QueryResults;
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

    @Test
    public void resultFetch() {
        // List
        List<Member> fetch = queryFactory
            .selectFrom(member)
            .fetch();

        // 단 건
        Member findMember1 = queryFactory
            .selectFrom(member)
            .fetchOne();

        // 처음 한 건 조회
        Member findMember2 = queryFactory
            .selectFrom(member)
            .fetchFirst();

        // 페이징에서 사용
        QueryResults<Member> results = queryFactory
            .selectFrom(member)
            .fetchResults();

        // count 쿼리로 변경
        long count = queryFactory
            .selectFrom(member)
            .fetchCount();
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last) */
    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
            .selectFrom(member)
            .where(member.age.eq(100))
            .orderBy(member.age.desc(), member.username.asc().nullsLast())
            .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }
}