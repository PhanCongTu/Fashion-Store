package personal.tu.fashionstore.dtos.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import personal.tu.fashionstore.dtos.Category.CategoryDto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDto {

    private String productName;

    private int quantity;

    private String description;

    private int price;

    private CategoryDto category;
}
