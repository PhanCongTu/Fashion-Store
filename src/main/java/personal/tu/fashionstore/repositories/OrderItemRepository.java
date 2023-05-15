package personal.tu.fashionstore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import personal.tu.fashionstore.entities.OrderItem;

public interface OrderItemRepository extends MongoRepository<OrderItem, String> {
    Page<OrderItem> findAllByOrderId(String orderId, Pageable pageable);
}
