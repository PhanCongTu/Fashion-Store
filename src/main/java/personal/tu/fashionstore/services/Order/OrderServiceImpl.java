package personal.tu.fashionstore.services.Order;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import personal.tu.fashionstore.dtos.Order.OrderDto;
import personal.tu.fashionstore.dtos.Order.RevenueDto;
import personal.tu.fashionstore.entities.Order;
import personal.tu.fashionstore.exceptions.NotFoundException;
import personal.tu.fashionstore.repositories.OrderRepository;
import personal.tu.fashionstore.services.Mail.IMailService;
import personal.tu.fashionstore.untils.EnumOrderStatus;
import personal.tu.fashionstore.untils.PageUtils;

import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final IMailService iMailService;
    private ModelMapper modelMapper;

    @Override
    public Page<OrderDto> filter(String searchText, String status, int page, int size, String sort, String column) {
        Pageable pageable = PageUtils.createPageable(page, size, sort, column);
        Page<Order> orders = orderRepository.findAllByAddressContainingAndStatusContainingOrPhoneNumberContainingAndStatusContainingAllIgnoreCase(searchText, status, searchText, status, pageable);
        return orders.map(order -> modelMapper.map(order, OrderDto.class));


    }

    @Override
    public List<OrderDto> getAllOrder(String userId) {
        List<Order> orders = orderRepository.findAllByUserIdOrderByCreateAtDesc(userId);
        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order oder : orders
        ) {
            orderDtos.add(modelMapper.map(oder, OrderDto.class));
        }
        return orderDtos;
    }


    @Override
    public OrderDto newOrder(OrderDto orderDto) {
        Order order = modelMapper.map(orderDto, Order.class);
        Order savedOrder = orderRepository.save(order);
        return modelMapper.map(savedOrder, OrderDto.class);

    }
    
    @Override
    public OrderDto updateStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) throw new NotFoundException("Không tìm thấy order có ID là: " + orderId);

        order.setStatus(status);
        Order newOrder = orderRepository.save(order);
        OrderDto newOrderDto = modelMapper.map(newOrder, OrderDto.class);
        iMailService.sendUpdateStatusOrderMail(newOrderDto);
        return newOrderDto;
    }

    @Override
    public OrderDto getOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) throw new NotFoundException("Không tìm thấy Order");
        return modelMapper.map(order, OrderDto.class);
    }

    @Override
    public Page<OrderDto> getRevenue(RevenueDto revenueDto,
                                     int page, int size, String sort, String column) {
        String status = EnumOrderStatus.DA_NHAN.name();
        Pageable pageable = PageUtils.createPageable(page, size, sort, column);
        Page<Order> orders = orderRepository.findAllByStatusContainingIgnoreCase(status, pageable);
        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            if (order.getStatus().equals(EnumOrderStatus.DA_NHAN.name())
                    && order.getCreateAt().after(revenueDto.getThoiGianBatDau())
                    && order.getCreateAt().before(revenueDto.getThoiGianKetThuc())) {
                orderDtos.add(modelMapper.map(order, OrderDto.class));
            }
        }
        return new PageImpl<>(orderDtos);
    }
}
