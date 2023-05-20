package personal.tu.fashionstore.services.Mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import personal.tu.fashionstore.Model.Mail;
import personal.tu.fashionstore.dtos.Order.OrderDto;
import personal.tu.fashionstore.dtos.OrderItem.OrderItemDto;
import personal.tu.fashionstore.dtos.User.UserDto;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class MaiServiceImp implements IMailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Override
    public void sendEmail(Mail mail) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setSubject(mail.getMailSubject());
            mimeMessageHelper.setFrom(new InternetAddress(mail.getMailFrom()));
            mimeMessageHelper.setTo(mail.getMailTo());
            mimeMessageHelper.setText(mail.getMailContent());
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    @Async
    @Override
    public void sendOrderMail(UserDto userDto, OrderDto orderDto, List<OrderItemDto> orderItemDtos) {
        /// Mail
        Mail mail = new Mail();
        mail.setMailFrom("phancongtu25032002@gmail.com");
        mail.setMailTo(userDto.getEmail());
        mail.setMailSubject("Fashion Store - Bạn đặt hàng thành công");
        String Content = "Tổng số tiền của đơn hàng của bạn là: " + orderDto.getTotal() +" VNĐ\n";
        int index = 1;
        for (OrderItemDto OID : orderItemDtos) {
            Content += index +". Tên sản phẩm: " +  OID.getProduct().getProductName()
                    +", số lượng: " +OID.getQuantity()+", kích thước: "+OID.getSize()+ "\n";
            index ++;
        }
        Content += "Sản phẩm được đặt bởi: " + userDto.getUserName() + "\n";
        Content += "Sản phẩm được đưa tới địa chỉ: " + orderDto.getAddress()+ "\n";
        Content += "Số điện thoại nhận hàng: " + orderDto.getPhoneNumber()+ "\n";
        Content += "Fashion Store xin chân thành cảm ơn!";
        mail.setMailContent(Content);
        sendEmail(mail);
    }
    @Override
    @Async
    public void sendUpdateStatusOrderMail(OrderDto orderDto) {
        /// Mail
        Mail mail = new Mail();
        mail.setMailFrom("phancongtu25032002@gmail.com");
        mail.setMailTo(orderDto.getUser().getEmail());
        mail.setMailSubject("Fashion Store - Đơn hàng của bạn đã được cập nhật");
        String newStatus ="";
        switch (orderDto.getStatus()) {
            case "CHO_XAC_NHAN":
                newStatus = "CHỜ XÁC NHẬN";
                break;
            case "DA_CHUYEN_HANG":
                newStatus = "ĐÃ CHUYỂN HÀNG";
                break;
            case "DA_NHAN":
                newStatus = "ĐÃ NHẬN";
                break;
            default:
                newStatus = "ĐÃ HỦY";
        }
        String Content = "Đơn hàng được đặt lúc: " + orderDto.getCreateAt()+" của bạn đã được đổi trạng thái thành " +
                newStatus;
        Content += " Fashion Store xin chân thành cảm ơn!";
        mail.setMailContent(Content);
        sendEmail(mail);
    }
    @Override
    @Async
    public void sendCodeForgetPassword(String email, String resetCode) {
        Mail mail = new Mail();
        mail.setMailFrom("phancongtu25032002@gmail.com");
        mail.setMailTo(email);
        mail.setMailSubject("Fashion Store - Đặt lại mật khẩu");
        String Content = "Mã đặt lại mật khẩu của bạn là (4 số): " + resetCode;
        mail.setMailContent(Content);
        sendEmail(mail);
    }
}
