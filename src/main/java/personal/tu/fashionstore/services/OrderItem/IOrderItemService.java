package personal.tu.fashionstore.services.OrderItem;

import org.springframework.data.domain.Page;
import personal.tu.fashionstore.dtos.OrderItem.OrderItemDto;
import personal.tu.fashionstore.entities.OrderItem;

public interface IOrderItemService {
    Page<OrderItemDto> getALlOrderItemByOrderId(String orderId, int page, int size, String sort, String column);

    OrderItemDto addOrderItem(OrderItem orderItem);
}
