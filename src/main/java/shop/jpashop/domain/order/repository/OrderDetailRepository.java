package shop.jpashop.domain.order.repository;

import java.util.List;
import java.util.Optional;
import shop.jpashop.domain.order.dto.OrderDetailSearchDto;
import shop.jpashop.domain.order.entity.Order;
import shop.jpashop.domain.order.entity.OrderDetail;

public interface OrderDetailRepository {

    Long save(OrderDetail orderDetail);

    Optional<OrderDetail> findById(Long id);

    List<OrderDetailSearchDto> searchOrderDetails(Order order);

    List<OrderDetail> findByOrder(Order order);

}
