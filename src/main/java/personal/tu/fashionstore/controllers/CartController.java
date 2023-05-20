package personal.tu.fashionstore.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import personal.tu.fashionstore.dtos.Cart.CartDto;
import personal.tu.fashionstore.dtos.Cart.CreateCartDto;
import personal.tu.fashionstore.dtos.Product.ProductDto;
import personal.tu.fashionstore.dtos.User.UserDto;
import personal.tu.fashionstore.services.Cart.ICartService;
import personal.tu.fashionstore.services.Product.IProductService;
import personal.tu.fashionstore.services.User.IUserService;
import java.security.Principal;


@RestController
@RequestMapping("/api/cart")
public class CartController {
    ICartService iCartService;
    IProductService iproductService;
    IUserService iUserService;

    public CartController(ICartService iCartService, IProductService iproductService, IUserService iUserService) {
        this.iCartService = iCartService;
        this.iproductService = iproductService;
        this.iUserService = iUserService;
    }
    /***
     *
     * @param userId: ID người dùng muốn xem cart
     * @param page: Số thứ tự của trang
     * @param column: Field muốn sắp xếp theo
     * @param size: Số lượng kết quả của 1 trang
     * @param sortType: sắp xếp theo:
     *                 true => tăng dần,
     *                 false => giảm dần
     * @return: Trả về 1 page các product dựa trên các thông tin đầu vào
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CartDto>> getAllCartByUser(@PathVariable("userId") String userId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "createAt") String column,
                                                    @RequestParam(defaultValue = "12") int size,
                                                    @RequestParam(defaultValue = "true") boolean sortType) {
        String sort = (sortType ? "asc" : "desc") ;
        return new ResponseEntity<>(iCartService.getAllCartByUserId(userId, page, size, sort, column), HttpStatus.OK);
    }
    /***
     * Lấy cart của người đang đăng nhập
     * @param principal: lấy thông tin người đang đăng nhập từ JWT
     * @param page: Số thứ tự của trang
     * @param column: Field muốn sắp xếp theo
     * @param size: Số lượng kết quả của 1 trang
     * @param sortType: sắp xếp theo:
     *                 true => tăng dần,
     *                 false => giảm dần
     * @return: Trả về 1 page các product dựa trên các thông tin đầu vào
     */
    @GetMapping("/my-cart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<CartDto>> getMyCart(Principal principal,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "createAt") String column,
                                                   @RequestParam(defaultValue = "12") int size,
                                                   @RequestParam(defaultValue = "true") boolean sortType) {
        UserDto userDto = iUserService.getUserByUserName(principal.getName());
        String sort = (sortType ? "asc" : "desc");
        return new ResponseEntity<>(iCartService.getAllCartByUserId(userDto.getId(), page, size, sort, column), HttpStatus.OK);

    }

    /***
     *
     * @param createCartDto: DTO chứa thông tin của cart muốn thêm (hoặc sửa)
     * @param principal: lấy thông tin người đang đăng nhập từ JWT
     * @return : Trả về cart mới thêm (hoặc sửa)
     */
    @PostMapping("/add-update")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<CartDto> createOrUpdateCart(@RequestBody CreateCartDto createCartDto,
                                                      Principal principal) {
        UserDto userDto = iUserService.getUserByUserName(principal.getName());
        ProductDto productDto = iproductService.getProductById(createCartDto.getProductId());
        CartDto newCartDto = new CartDto();
        if(createCartDto.getQuantity()<=0){
            newCartDto.setQuantity(1);
        }
        else{
            newCartDto.setQuantity(createCartDto.getQuantity());
        }
        newCartDto.setProduct(productDto);
        newCartDto.setSize(createCartDto.getSize());
        CartDto savedCartDto = iCartService.addOrUpdateCart(newCartDto, userDto.getId());
        return new ResponseEntity<>(savedCartDto, HttpStatus.CREATED);
    }

    /***
     *
     * @param cartId: ID của cart muốn xóa
     * @return: Thông báo thành công
     */
    @DeleteMapping("/delete/{cartId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteCart(@PathVariable("cartId") String cartId) {
        iCartService.deleteCart(cartId);
        return new ResponseEntity<>("Deleted successfully!!", HttpStatus.OK);
    }

    /***
     *
     * @param userId: ID của user mà admin muốn đếm số cart
     * @return: Số cart của user đó
     */
    @GetMapping("/count/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> countAllCartByUser(@PathVariable String userId){
        return new ResponseEntity<>(iCartService.countAllCartByUserId(userId), HttpStatus.OK);
    }

    /***
     *
     * @param principal: lấy thông tin người đang đăng nhập từ JWT
     * @return: Số cart của bản thân người đang đăng nhập
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Integer> countMyCart(Principal principal){
        UserDto userDto = iUserService.getUserByUserName(principal.getName());
        return new ResponseEntity<>(iCartService.countAllCartByUserId(userDto.getId()), HttpStatus.OK);
    }
}
