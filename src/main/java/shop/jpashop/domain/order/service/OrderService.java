package shop.jpashop.domain.order.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import shop.jpashop.domain.member.entity.Member;
import shop.jpashop.domain.order.dto.OrderSearchDto;
import shop.jpashop.domain.order.entity.Order;
import shop.jpashop.domain.order.entity.OrderDetail;
import shop.jpashop.domain.order.repository.OrderDetailRepository;
import shop.jpashop.domain.order.repository.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public Long register(Order order) {
        Long savedOrderId = orderRepository.save(order);
        Order findOrder = findOne(savedOrderId);

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(findOrder);
        orderDetails.forEach(orderDetailRepository::save);

        return orderRepository.save(order);
    }

    public Order findOne(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 주문을 찾을 수 없습니다."));
    }

    public Page<OrderSearchDto> searchOrders(int page, Member member) {
        PageRequest pageRequest = PageRequest.of(page, 5);
        return orderRepository.searchOrders(member, pageRequest);
    }

}
