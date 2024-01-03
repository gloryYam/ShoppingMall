package shop.jpashop.domain.cart.repository;

import shop.jpashop.domain.cart.entity.Cart;
import shop.jpashop.domain.member.entity.Member;

public interface CartRepository {

    Long save(Cart cart);

    Cart findByMember(Member member);

}
