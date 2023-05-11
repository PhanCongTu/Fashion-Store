package personal.tu.fashionstore.services.User;


import org.springframework.data.domain.Page;
import personal.tu.fashionstore.dtos.SignUp;
import personal.tu.fashionstore.dtos.User.ChangePasswordDto;
import personal.tu.fashionstore.dtos.User.UpdateUserDto;
import personal.tu.fashionstore.dtos.User.UserDto;

import java.security.Principal;

public interface IUserService {
    UserDto createUser(SignUp signUp);

    UserDto getUserByUserNameAndPassword(String userName, String password);

    void upgradeRole(String userId);

    Page<UserDto> filter(String search, int page, int size,
                         String sort, String column);

    UserDto getUserById(String userId);

    Boolean checkExistByUsername(String username);

    UserDto updateUser(Principal principal, UpdateUserDto userDto);

    void changeStatusUser(String userId);

    UserDto getUserByUserName(String userName);

    void deleteUser(String userId);

    void changePassword(ChangePasswordDto userDto, Principal principal);

    UserDto getMyInf(Principal principal);

    boolean resetPassword(String username, String newPass);
}
