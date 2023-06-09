package personal.tu.fashionstore.dtos.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import personal.tu.fashionstore.dtos.Category.CategoryDto;
import personal.tu.fashionstore.dtos.ProductImage.ProductImageDto;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String id;
    private String productName;
    private int quantity;
    private int sold;
    private String description;
    private int price;
    private Boolean isActive = true;
    private CategoryDto category;
    private List<ProductImageDto> images;
}
