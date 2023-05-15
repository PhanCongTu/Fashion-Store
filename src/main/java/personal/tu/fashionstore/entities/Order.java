package personal.tu.fashionstore.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order")
public class Order {
    private String id;

    private String address;

    private String phoneNumber;

    private String note;

    private String status;

    private double total;

    private Date createAt = new Date(new java.util.Date().getTime());
    @DBRef
    private User user;
    @DBRef
    private List<OrderItem> orderItems;
}
