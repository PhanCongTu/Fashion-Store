package personal.tu.fashionstore.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "category")
public class Category {
    @Id
    private String id;
    private String name;
    private String image;
    private Boolean isDeleted = false;
    private Date createAt= new Date(new java.util.Date().getTime());
    private Date updateAt= new Date(new java.util.Date().getTime());
}
