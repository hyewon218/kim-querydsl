package study.kimquerydsl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KimQuerydslApplication {

    public static void main(String[] args) {
        SpringApplication.run(KimQuerydslApplication.class, args);
    }

    /*  // JPAQueryFactory 를 스프링 빈으로 등록해서 주입받아 사용해도 된다.
    @Bean
    JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }*/
}
