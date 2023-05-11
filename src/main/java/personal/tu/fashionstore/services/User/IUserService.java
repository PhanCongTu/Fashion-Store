package personal.tu.fashionstore.services.User;


import personal.tu.fashionstore.dtos.SignUp;
import personal.tu.fashionstore.dtos.User.UserDto;

public interface IUserService {
    UserDto createUser(SignUp signUp);

    UserDto getUserByUserNameAndPassword(String userName, String password);

    void upgradeRole(String userId);
}
