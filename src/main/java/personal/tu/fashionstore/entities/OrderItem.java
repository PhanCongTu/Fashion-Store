package personal.tu.fashionstore.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order-item")
public class OrderItem {
    private String id;
    private int quantity;
    private String size;
    private Date createAt = new Date(new java.util.Date().getTime());
    @DBRef
    private Order order;
    @DBRef
    private Product product;
}
