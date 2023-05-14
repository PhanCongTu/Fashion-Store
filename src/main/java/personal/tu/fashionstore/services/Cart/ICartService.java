package personal.tu.fashionstore.services.Cart;

import org.springframework.data.domain.Page;
import personal.tu.fashionstore.dtos.Cart.CartDto;

import java.util.List;

public interface ICartService {


    Page<CartDto> getAllCartByUserId(String userId, int page, int size, String sort, String column);

    CartDto getCartById(String cartId);

    CartDto addOrUpdateCart(CartDto cartDto, String userId);

    int countAllCartByUserId(String userId);

    void deleteCart(String cartId);
}
