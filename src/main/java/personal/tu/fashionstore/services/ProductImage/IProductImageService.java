package personal.tu.fashionstore.services.ProductImage;

import org.springframework.data.domain.Page;
import personal.tu.fashionstore.dtos.ProductImage.ProductImageDto;

public interface IProductImageService {
    Page<ProductImageDto> filter(String productId, int page, int size, String sort, String column);

    ProductImageDto addProductImage(ProductImageDto productImageDto, String productId);

    void deleteProductImage(String pImage);
}
