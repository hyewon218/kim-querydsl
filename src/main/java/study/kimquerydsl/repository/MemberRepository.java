package study.kimquerydsl.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import study.kimquerydsl.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom,
    QuerydslPredicateExecutor<Member> {
    // select m from Member m where m.username = ?
    List<Member> findByUsername(String username);
}