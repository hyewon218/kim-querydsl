package study.kimquerydsl;

import static org.assertj.core.api.Assertions.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.kimquerydsl.entity.Hello;
import study.kimquerydsl.entity.QHello;

@SpringBootTest
@Transactional
@Commit
class KimQuerydslApplicationTests {

    @Autowired
    EntityManager em;

    @Test
    void contextLoads() {
        Hello hello = new Hello();
        em.persist(hello);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QHello qHello = QHello.hello;// Querydsl Q타입 동작 확인

        Hello result = query
            .selectFrom(qHello)
            .fetchOne();

        assertThat(result).isEqualTo(hello);

        //lombok 동작 확인 (hello.getId())
        assertThat(result.getId()).isEqualTo(hello.getId());
    }
}
