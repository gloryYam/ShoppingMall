package shop.jpashop.repository.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.jpashop.domain.cart.entity.Cart;
import shop.jpashop.domain.member.entity.Member;

public interface CartJpaRepository extends JpaRepository<Cart, Long> {
    Cart findByMember(Member member);

}
