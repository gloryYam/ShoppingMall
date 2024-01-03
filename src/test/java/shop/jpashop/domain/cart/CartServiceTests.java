package shop.jpashop.domain.cart;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shop.jpashop.domain.cart.entity.Cart;
import shop.jpashop.domain.cart.service.CartService;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.domain.member.repository.MemberRepository;
import shop.jpashop.repository.cart.CartJpaRepository;

@Transactional
@SpringBootTest
class CartServiceTests {

    @Autowired
    CartService sut;

    @Autowired
    CartJpaRepository cartJpaRepository;

    @Autowired
    MemberRepository memberRepository;

    private Member saveMember() {
        Member member = Member.builder()
                .email("test@test.com")
                .build();
        memberRepository.save(member);
        return member;
    }

    @Test
    void 장바구니_조회() {
        // given
        Member member = saveMember();

        // when
        Cart cart = sut.findCart(member.getEmail());

        // then
        assertNotNull(cartJpaRepository.findById(cart.getId()));
    }

}