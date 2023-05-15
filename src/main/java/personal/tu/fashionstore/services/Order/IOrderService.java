package personal.tu.fashionstore.services.Order;

import org.springframework.data.domain.Page;
import personal.tu.fashionstore.dtos.Order.OrderDto;
import personal.tu.fashionstore.dtos.Order.RevenueDto;

import java.util.List;

public interface IOrderService {
    Page<OrderDto> filter(String searchText, String status, int page, int size, String sort, String column);

    List<OrderDto> getAllOrder(String userId);

    OrderDto newOrder(OrderDto orderDto);

    OrderDto updateStatus(String orderId, String status);

    OrderDto getOrder(String orderId);

    Page<OrderDto> getRevenue(RevenueDto revenueDto,
                              int page, int size, String sort, String column);
}
