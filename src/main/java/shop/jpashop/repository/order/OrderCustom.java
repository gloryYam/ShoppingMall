package shop.jpashop.repository.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.domain.order.dto.OrderSearchDto;

public interface OrderCustom {

    Page<OrderSearchDto> searchOrders(Member member, Pageable pageable);

}
