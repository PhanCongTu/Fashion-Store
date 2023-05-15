package personal.tu.fashionstore.dtos.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddOrderDto {
    private String address;

    private String phoneNumber;

    private String note;

    private List<String> idCarts;
}

