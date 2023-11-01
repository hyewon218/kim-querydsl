package study.kimquerydsl.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import study.kimquerydsl.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    // select m from Member m where m.username = ?
    List<Member> findByUsername(String username);
}