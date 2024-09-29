package jpabook.jpashop.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;

import org.assertj.core.api.Assertions;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
    void 회원가입() throws Exception {
        Member member = new Member();
        member.setName("kim");

        Long memberId = memberService.join(member);

        Assertions.assertThat(member).isEqualTo(memberRepository.findById(memberId).get());
    }

    @Test
    void 중복_회원_예외() throws Exception {
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");

        memberService.join(member1);

        Throwable thrown = Assertions.catchThrowable(() -> {
            memberService.join(member2); // 예외발생해야 한다
        });

        // then
        Assertions.assertThat(thrown).isInstanceOf(IllegalStateException.class).hasMessageContaining("이미 존재하는 회원입니다.");

    }
}
