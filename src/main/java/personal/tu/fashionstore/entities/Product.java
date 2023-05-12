package personal.tu.fashionstore.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product")
public class Product {
    @Id
    private String id;
    private String productName;
    private int quantity;
    private int sold = 0;
    private String description;
    private int price;
    private Boolean isActive = true;
    private Date createAt = new Date(new java.util.Date().getTime());
    private Date updateAt= new Date(new java.util.Date().getTime());
    @DBRef
    private Category category;
}
