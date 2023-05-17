package personal.tu.fashionstore.services.Mail;

import org.springframework.scheduling.annotation.Async;
import personal.tu.fashionstore.Model.Mail;
import personal.tu.fashionstore.dtos.Order.OrderDto;
import personal.tu.fashionstore.dtos.OrderItem.OrderItemDto;
import personal.tu.fashionstore.dtos.User.UserDto;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IMailService {
     void sendEmail(Mail mail);
     void sendOrderMail(UserDto userDto, OrderDto orderDto, List<OrderItemDto> orderItemDtos);

    @Async
    void sendUpdateStatusOrderMail(OrderDto orderDto);

    @Async
    void sendCodeForgetPassword(String email, String resetCode);
}
