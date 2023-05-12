package personal.tu.fashionstore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import personal.tu.fashionstore.entities.Category;
import personal.tu.fashionstore.entities.Product;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    Page<Product> findByProductNameContainingIgnoreCaseAndIsActive(String productName, Boolean booleen, Pageable pageable);
    Page<Product> findByProductNameContainingAndCategoryAllIgnoreCaseAndIsActive(String productName, Category category, Boolean booleen, Pageable pageable);
    List<Product> findTop8ByOrderBySoldDesc();
    List<Product> findTop8ByOrderByCreateAtDesc();
    List<Product> findByCategoryId(String category);
}
