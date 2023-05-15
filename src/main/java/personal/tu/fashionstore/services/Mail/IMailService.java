package personal.tu.fashionstore.services.Mail;

import org.springframework.scheduling.annotation.Async;
import personal.tu.fashionstore.Model.Mail;
import personal.tu.fashionstore.dtos.Order.OrderDto;
import personal.tu.fashionstore.dtos.OrderItem.OrderItemDto;
import personal.tu.fashionstore.dtos.User.UserDto;
import java.util.List;

public interface IMailService {
    public void sendEmail(Mail mail);
    public void sendOrderMail(UserDto userDto, OrderDto orderDto, List<OrderItemDto> orderItemDtos);

    @Async
    void sendUpdateStatusOrderMail(OrderDto orderDto);
}
