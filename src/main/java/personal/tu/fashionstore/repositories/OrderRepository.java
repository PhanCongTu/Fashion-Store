package personal.tu.fashionstore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import personal.tu.fashionstore.entities.Order;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findAllByUserIdOrderByCreateAtDesc(String userId);
    Page<Order> findAllByAddressContainingAndStatusContainingOrPhoneNumberContainingAndStatusContainingAllIgnoreCase(String address, String status1,String phoneNumber, String status2, Pageable pageable );
    Page<Order> findAllByStatusContainingIgnoreCase(String status, Pageable pageable);
}
