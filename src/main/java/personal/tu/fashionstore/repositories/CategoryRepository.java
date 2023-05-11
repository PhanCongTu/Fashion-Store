package personal.tu.fashionstore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import personal.tu.fashionstore.entities.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
