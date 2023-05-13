package personal.tu.fashionstore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import personal.tu.fashionstore.entities.Product;
import personal.tu.fashionstore.entities.ProductImage;

public interface ProductImageRepository extends MongoRepository<ProductImage, String> {
    Page<ProductImage> findAllByProduct(Product product, Pageable pageable);
}
