package personal.tu.fashionstore.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user")
public class User {
    @Id
    private String id;
    private String name;
    private String userName;
    @JsonIgnore
    private String password;
    private String avatar;
    private String phoneNumber;
    @Email
    private String email;
    private Boolean isActive = true;
    private Date createAt= new Date(new java.util.Date().getTime());
    private Date updateAt= new Date(new java.util.Date().getTime());
    private List<String> roles = new ArrayList<>();

    public User(String name, String userName, String password, String phoneNumber, String email, List<String> roles) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.roles = roles;
    }
}
