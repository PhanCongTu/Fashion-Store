package personal.tu.fashionstore.dtos.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import personal.tu.fashionstore.dtos.OrderItem.OrderItemDto;
import personal.tu.fashionstore.dtos.User.UserDto;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private String id;
    private String address;
    private String phoneNumber;
    private String note;
    private String status;
    private double total;
    private Date createAt;
    private UserDto user;
    private List<OrderItemDto> orderItems;
}
