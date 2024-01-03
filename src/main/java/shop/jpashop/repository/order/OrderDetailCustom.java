package shop.jpashop.repository.order;

import java.util.List;
import shop.jpashop.domain.order.dto.OrderDetailSearchDto;
import shop.jpashop.domain.order.entity.Order;

public interface OrderDetailCustom {

    List<OrderDetailSearchDto> searchOrderDetails(Order order);

}
