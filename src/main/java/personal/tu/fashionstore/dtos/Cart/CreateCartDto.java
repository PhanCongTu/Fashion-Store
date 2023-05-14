package personal.tu.fashionstore.dtos.Cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCartDto {
    private String productId;
    private int quantity;
    private String size;
}
