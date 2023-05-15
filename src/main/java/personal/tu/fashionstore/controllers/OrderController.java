package personal.tu.fashionstore.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import personal.tu.fashionstore.dtos.Cart.CartDto;
import personal.tu.fashionstore.dtos.Order.AddOrderDto;
import personal.tu.fashionstore.dtos.Order.OrderDto;
import personal.tu.fashionstore.dtos.OrderItem.OrderItemDto;
import personal.tu.fashionstore.dtos.User.UserDto;
import personal.tu.fashionstore.entities.Order;
import personal.tu.fashionstore.entities.OrderItem;
import personal.tu.fashionstore.entities.Product;
import personal.tu.fashionstore.repositories.OrderRepository;
import personal.tu.fashionstore.services.Cart.ICartService;
import personal.tu.fashionstore.services.Mail.IMailService;
import personal.tu.fashionstore.services.Order.IOrderService;
import personal.tu.fashionstore.services.OrderItem.IOrderItemService;
import personal.tu.fashionstore.services.Product.IProductService;
import personal.tu.fashionstore.services.User.IUserService;
import personal.tu.fashionstore.untils.EnumOrderStatus;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    final IOrderService iOrderService;
    final ICartService iCartService;
    final IUserService iUserService;
    final IOrderItemService iOrderItemService;
    final IProductService iProductService;
    final IMailService iMailService;
    final ModelMapper modelMapper;
    private final OrderRepository orderRepository;

    public OrderController(IOrderService iOrderService, ICartService iCartService, IUserService iUserService, IOrderItemService iOrderItemService, IProductService iProductService, IMailService iMailService, ModelMapper modelMapper, OrderRepository orderRepository) {
        this.iOrderService = iOrderService;
        this.iCartService = iCartService;
        this.iUserService = iUserService;
        this.iOrderItemService = iOrderItemService;
        this.iProductService = iProductService;
        this.iMailService = iMailService;
        this.modelMapper = modelMapper;
        this.orderRepository = orderRepository;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDto>> getAllOrders(@RequestParam(defaultValue = "4") int IStatus,
                                                       @RequestParam(defaultValue = "") String searchText,
                                                       @RequestParam(defaultValue = "createAt") String column,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "12") int size,
                                                       @RequestParam(defaultValue = "true") boolean sortType) {
        String status;
        String sort = (sortType ? "asc" : "desc");
        switch (IStatus) {
            case 0:
                status = EnumOrderStatus.CHO_XAC_NHAN.name();
                break;
            case 1:
                status = EnumOrderStatus.DA_CHUYEN_HANG.name();
                break;
            case 2:
                status = EnumOrderStatus.DA_NHAN.name();
                break;
            case 3:
                status = EnumOrderStatus.DA_HUY.name();
                break;
            default:
                status = "";
        }
        return new ResponseEntity<>(iOrderService.filter(searchText.trim(), status, page, size, sort, column), HttpStatus.OK);
    }
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable String orderId){
        return new ResponseEntity<>(iOrderService.getOrder(orderId), HttpStatus.OK);
    }
    @PutMapping("/update-status")
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<OrderDto> UpdateStatusOrder(@RequestParam String orderId,
                                                  @RequestParam int Istatus ){
        String status;
        switch (Istatus) {
            case 1:
                status = EnumOrderStatus.DA_CHUYEN_HANG.name();
                break;
            case 2:
                status = EnumOrderStatus.DA_NHAN.name();
                break;
            case 3:
                status = EnumOrderStatus.DA_HUY.name();
                break;
            default:
                status = EnumOrderStatus.CHO_XAC_NHAN.name();
        }
        return new ResponseEntity<>(iOrderService.updateStatus(orderId, status), HttpStatus.OK);
    }
    /***
     *  Controller dành cho admin để xem order của user
     * @param userId : ID của user
     * @return : Danh sách các order
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@PathVariable String userId) {
        List<OrderDto> orderDtos = iOrderService.getAllOrder(userId);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    /***
     *  Controller dành cho user để lấy order của mình
     * @param principal : lấy từ token
     * @return : Danh sách các order
     */
    @GetMapping("/my-order")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderDto>> getMyOrder(Principal principal) {
        UserDto userDto = iUserService.getUserByUserName(principal.getName());
        System.out.println(userDto);
        List<OrderDto> orderDtos = iOrderService.getAllOrder(userDto.getId());
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    /***]
     * @author : Tu
     * @param principal : lấy từ token (Chỉ user mới được đặt hàng)
     * @param addOrderDto : Truyền vào thông tin cần thiết
     * @return : Thông tin về order mơi
     */
    @PostMapping("/add")
    public ResponseEntity<OrderDto> addToOrder(Principal principal,
                                               @RequestBody AddOrderDto addOrderDto) {
        UserDto loginedUser = iUserService.getUserByUserName(principal.getName());
        List<CartDto> cartsWantToBuy = new ArrayList<>();

        double totalMoney = 0;

        // Danh sách id các cart muốn order
        List<String> idCarts = addOrderDto.getIdCarts();
        // Nếu không truyền gì thì mặc định sẽ order tất cả item có trong cart
        if (idCarts.isEmpty()) {
            cartsWantToBuy = iCartService.getAllCartByUserId(loginedUser.getId());
        } else {
            for (String id : idCarts) {
                CartDto cartDto = iCartService.getCartById(id);
                if (cartDto != null) {
                    cartsWantToBuy.add(cartDto);
                }
            }
        }

        for (CartDto cartDto : cartsWantToBuy) {
            totalMoney += cartDto.getProduct().getPrice() * cartDto.getQuantity();
        }


        OrderDto orderDto = new OrderDto();
        orderDto.setUser(loginedUser);
        orderDto.setTotal(totalMoney);
        orderDto.setAddress(addOrderDto.getAddress());
        orderDto.setPhoneNumber(addOrderDto.getPhoneNumber());
        orderDto.setStatus(EnumOrderStatus.CHO_XAC_NHAN.name());
        orderDto.setNote(addOrderDto.getNote());
        orderDto.setCreateAt(new Date(new java.util.Date().getTime()));
        OrderDto newOrder = iOrderService.newOrder(orderDto);

        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        // Lọc qua các item trong cart muốn và thêm mới các item của order
        for (CartDto cartDto : cartsWantToBuy) {

            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setSize(cartDto.getSize());
            newOrderItem.setQuantity(cartDto.getQuantity());
            newOrderItem.setOrder(modelMapper.map(newOrder, Order.class));
            newOrderItem.setProduct(modelMapper.map(cartDto.getProduct(), Product.class));

            OrderItemDto newOrderItemDto = iOrderItemService.addOrderItem(newOrderItem);
            orderItemDtos.add(newOrderItemDto);
            iCartService.deleteCart(cartDto.getId());
        }

        newOrder.setOrderItems(orderItemDtos);
        orderRepository.save(modelMapper.map(newOrder, Order.class));
        iMailService.sendOrderMail(loginedUser, newOrder,orderItemDtos);
        return new ResponseEntity<>(newOrder, HttpStatus.OK);
    }
}
