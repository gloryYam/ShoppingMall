package shop.jpashop.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.jpashop.domain.order.entity.Order;

public interface OrderJpaRepository extends JpaRepository<Order, Long>, OrderCustom {

}
