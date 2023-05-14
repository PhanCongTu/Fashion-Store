package personal.tu.fashionstore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import personal.tu.fashionstore.entities.Cart;

import java.util.List;

public interface CartRepository extends MongoRepository<Cart, String> {
    List<Cart> findAllByUserId(String UserId);

    Page<Cart> findAllByUserId(String UserId, Pageable pageable);
    int countByUserId(String UserId);
    void deleteById(String cartId);
}