package shop.jpashop.repository.order;

import static shop.jpashop.domain.item.entity.QItem.item;
import static shop.jpashop.domain.order.entity.QOrderDetail.orderDetail;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import shop.jpashop.domain.item.dto.ItemSearchDto;
import shop.jpashop.domain.item.repository.ItemRepository;
import shop.jpashop.domain.order.dto.OrderDetailSearchDto;
import shop.jpashop.domain.order.dto.QOrderDetailSearchDto;
import shop.jpashop.domain.order.entity.Order;

@RequiredArgsConstructor
public class OrderDetailCustomImpl implements OrderDetailCustom {

    private final JPAQueryFactory queryFactory;
    private final ItemRepository itemRepository;

    @Override
    public List<OrderDetailSearchDto> searchOrderDetails(Order order) {
        QOrderDetailSearchDto qOrderDetailSearchDto = new QOrderDetailSearchDto(orderDetail.id, item.id, orderDetail.quantity);
        List<OrderDetailSearchDto> orderDetailSearchDtoList = queryFactory
                .select(qOrderDetailSearchDto)
                .from(orderDetail)
                .where(orderDetail.order.eq(order))
                .join(orderDetail.item, item)
                .fetch();

        orderDetailSearchDtoList.forEach(this::injectItem);

        return orderDetailSearchDtoList;
    }

    private void injectItem(OrderDetailSearchDto orderDetailSearchDto) {
        Long itemId = orderDetailSearchDto.getItemId();
        ItemSearchDto itemSearchDto = itemRepository.findItemDtoById(itemId);
        orderDetailSearchDto.injectItemSearchDto(itemSearchDto);
    }

}
