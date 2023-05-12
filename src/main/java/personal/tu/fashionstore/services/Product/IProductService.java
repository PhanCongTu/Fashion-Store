package personal.tu.fashionstore.services.Product;

import org.springframework.data.domain.Page;
import personal.tu.fashionstore.dtos.Product.CreateProductDto;
import personal.tu.fashionstore.dtos.Product.ProductDto;
import personal.tu.fashionstore.dtos.Product.UpdateProductDto;

import java.util.List;

public interface IProductService {
    Page<ProductDto> filter(String searchText, String categoryId, int page, int size,
                            String sort, String column);

    ProductDto getProductById(String ProductId);

    ProductDto createProduct(CreateProductDto productDto);

    // Cập nhật lại Product (Cập nhật lại toàn bộ các thuộc tính)
    ProductDto updateProduct(String id, UpdateProductDto updateProductDto);

    void changeStatus(String ProductId);

    List<ProductDto> getTop8ProductBySold();

    List<ProductDto> getTop8NewProducts();

    List<ProductDto> getProductByCategoryId(String category);
}
