package personal.tu.fashionstore.dtos.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {

    private String name;

    private String avatar;

    private String phoneNumber;

    private String email;

}
