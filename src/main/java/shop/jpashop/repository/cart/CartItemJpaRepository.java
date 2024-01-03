package shop.jpashop.repository.cart;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.jpashop.domain.cart.entity.Cart;
import shop.jpashop.domain.cart.entity.CartItem;
import shop.jpashop.domain.item.entity.Item;

public interface CartItemJpaRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndItem(Cart cart, Item item);

    List<CartItem> findByCart(Cart cart);

}
