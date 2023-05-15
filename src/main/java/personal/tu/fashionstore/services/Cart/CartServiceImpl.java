package personal.tu.fashionstore.services.Cart;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import personal.tu.fashionstore.dtos.Cart.CartDto;
import personal.tu.fashionstore.entities.Cart;
import personal.tu.fashionstore.entities.User;
import personal.tu.fashionstore.exceptions.NotFoundException;
import personal.tu.fashionstore.repositories.CartRepository;
import personal.tu.fashionstore.services.User.IUserService;
import personal.tu.fashionstore.untils.PageUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartServiceImpl implements ICartService{
    private final CartRepository cartRepository;
    private final IUserService iUserService;
    private ModelMapper modelMapper;
    @Override
    public List<CartDto> getAllCartByUserId(String userId){
        List<Cart> carts = cartRepository.findAllByUserId(userId);
        return carts.stream()
                .map((cart) -> modelMapper.map(cart, CartDto.class))
                .collect(Collectors.toList());
    }
    @Override
    public Page<CartDto> getAllCartByUserId(String userId, int page, int size, String sort, String column) {
        Pageable pageable = PageUtils.createPageable(page, size, sort, column);
        Page<Cart> carts = cartRepository.findAllByUserId(userId,pageable);
        return carts.map(cart -> modelMapper.map(cart, CartDto.class));
    }

    @Override
    public CartDto getCartById(String cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) throw new NotFoundException("Cant find category!");
        return modelMapper.map(cart, CartDto.class);
    }

    @Override
    public CartDto addOrUpdateCart(CartDto cartDto, String userId){
        List<CartDto> carts = cartRepository.findAllByUserId(userId)
                        .stream().map(cart -> modelMapper.map(cart, CartDto.class))
                        .collect(Collectors.toList());
        int plus = cartDto.getQuantity();
        for (CartDto cart: carts) {
            if (Objects.equals(cart.getProduct().getId(), cartDto.getProduct().getId()) && cart.getSize().trim().equals(cartDto.getSize().trim())){
                cartDto = cart ;
                int newQuantity = cart.getQuantity() + plus;
                if(newQuantity<1) newQuantity =1;
                cartDto.setQuantity(newQuantity);
                break;
            }
        }
        Cart cart = modelMapper.map(cartDto, Cart.class);
        cart.setUser(modelMapper.map(iUserService.getUserById(userId), User.class));
        return modelMapper.map(cartRepository.save(cart), CartDto.class);
    }

    @Override
    public int countAllCartByUserId(String userId){
        return cartRepository.countByUserId(userId);
    }

    @Override
    public void deleteCart(String cartId){
        cartRepository.deleteById(cartId);
    }
}
