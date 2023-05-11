package personal.tu.fashionstore.services.User;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import personal.tu.fashionstore.dtos.SignUp;
import personal.tu.fashionstore.dtos.User.UserDto;
import personal.tu.fashionstore.entities.User;
import personal.tu.fashionstore.exceptions.InvalidException;
import personal.tu.fashionstore.exceptions.NotFoundException;
import personal.tu.fashionstore.repositories.UserRepository;
import personal.tu.fashionstore.untils.EnumRole;

import java.util.Arrays;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private ModelMapper modelMapper;
    @Override
    public UserDto createUser(SignUp signUp) {
        User User = modelMapper.map(signUp, User.class);
        if (userRepository.existsByUserName(signUp.getUserName().trim()))
            throw new InvalidException("Username existed !!");
        if (userRepository.existsByEmail(signUp.getEmail().trim())) throw new InvalidException("Email existed !!");
        User.setRoles(Arrays.asList(EnumRole.ROLE_USER.name()));
        User.setIsActive(true);
        return modelMapper.map(userRepository.save(User), UserDto.class);
    }
    @Override
    public UserDto getUserByUserNameAndPassword(String userName, String password) {
        User user = userRepository.findByUserNameAndPassword(userName, password);
        if (user == null) throw new NotFoundException("Wrong username or password !!");
        if (!user.getIsActive()) throw new NotFoundException("User has been deleted !");
        return modelMapper.map(user, UserDto.class);
    }
    @Override
    public void upgradeRole(String userId) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (!existingUser.isPresent()) throw new NotFoundException("User not found!");
        existingUser.get().getRoles().add(EnumRole.ROLE_ADMIN.name());
        userRepository.save(existingUser.get());
    }
}
