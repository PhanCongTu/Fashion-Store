package personal.tu.fashionstore.services.OrderItem;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import personal.tu.fashionstore.dtos.OrderItem.OrderItemDto;
import personal.tu.fashionstore.entities.OrderItem;
import personal.tu.fashionstore.entities.Product;
import personal.tu.fashionstore.exceptions.NotFoundException;
import personal.tu.fashionstore.repositories.OrderItemRepository;
import personal.tu.fashionstore.repositories.ProductRepository;
import personal.tu.fashionstore.services.Product.IProductService;
import personal.tu.fashionstore.untils.PageUtils;

@Service
@AllArgsConstructor
public class OrderItemServiceImpl implements IOrderItemService{
    private OrderItemRepository orderItemRepository;
    private ModelMapper modelMapper;
    private ProductRepository productRepository;

    @Override
    public Page<OrderItemDto> getALlOrderItemByOrderId(String orderId, int page, int size, String sort, String column){
        Pageable pageable = PageUtils.createPageable(page, size, sort, column);
        Page<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId,pageable);
        return orderItems.map(orderItem -> modelMapper.map(orderItem, OrderItemDto.class));
    }

    @Override
    public OrderItemDto addOrderItem(OrderItem orderItem){
        Product product = productRepository.findById(orderItem.getProduct().getId()).orElse(null);
        if (product == null) throw new NotFoundException("Không tìm thấy sản phẩm");
        product.setQuantity(product.getQuantity()-orderItem.getQuantity());
        product.setSold(product.getSold() + orderItem.getQuantity());
        productRepository.save(product);
        OrderItem savedCategory = orderItemRepository.save(orderItem);
        return modelMapper.map(savedCategory, OrderItemDto.class);
    }
}
