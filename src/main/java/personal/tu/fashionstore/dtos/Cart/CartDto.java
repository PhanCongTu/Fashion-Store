package personal.tu.fashionstore.dtos.Cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import personal.tu.fashionstore.dtos.Product.ProductDto;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private String id;

    private int quantity;

    private String size;

    private ProductDto product;
}
