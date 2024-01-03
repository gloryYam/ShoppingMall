package shop.jpashop.domain.order.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.domain.order.dto.OrderSearchDto;
import shop.jpashop.domain.order.entity.Order;

public interface OrderRepository {

    Long save(Order order);

    Optional<Order> findById(Long id);

    Page<OrderSearchDto> searchOrders(Member member, Pageable pageable);

}
