package personal.tu.fashionstore.services.User;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import personal.tu.fashionstore.dtos.SignUp;
import personal.tu.fashionstore.dtos.User.ChangePasswordDto;
import personal.tu.fashionstore.dtos.User.UpdateUserDto;
import personal.tu.fashionstore.dtos.User.UserDto;
import personal.tu.fashionstore.entities.User;
import personal.tu.fashionstore.exceptions.DuplicateKeyException;
import personal.tu.fashionstore.exceptions.InvalidException;
import personal.tu.fashionstore.exceptions.NotFoundException;
import personal.tu.fashionstore.repositories.UserRepository;
import personal.tu.fashionstore.services.Mail.IMailService;
import personal.tu.fashionstore.untils.EnumRole;
import personal.tu.fashionstore.untils.PageUtils;

import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final IMailService iMailService;
    private ModelMapper modelMapper;
    @Override
    public UserDto createUser(SignUp signUp) {
        User User = modelMapper.map(signUp, User.class);
        if (userRepository.existsByUserName(signUp.getUserName().trim()))
            throw new InvalidException("Username existed !!");
        if (userRepository.existsByEmail(signUp.getEmail().trim())) throw new InvalidException("Email existed !!");
        User.setRoles(Collections.singletonList(EnumRole.ROLE_USER.name()));
        User.setIsActive(true);
        return modelMapper.map(userRepository.save(User), UserDto.class);
    }
    @Override
    public UserDto getUserByUserNameAndPassword(String userName, String password) {
        User user = userRepository.findByUserNameAndPassword(userName, password);
        if (user == null) throw new NotFoundException("Wrong username or password !!");
        if (!user.getIsActive()) throw new NotFoundException("User has been unactivated !");
        return modelMapper.map(user, UserDto.class);
    }
    @Override
    public void upgradeRole(String userId) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (!existingUser.isPresent()) throw new NotFoundException("User not found!");
        existingUser.get().getRoles().add(EnumRole.ROLE_ADMIN.name());
        userRepository.save(existingUser.get());
    }

    @Override
    public Page<UserDto> filter(String search, int page, int size,
                                String sort, String column) {
        Pageable pageable = PageUtils.createPageable(page, size, sort, column);
        Page<User> users = userRepository.findByNameContainingOrEmailContainingOrPhoneNumberContainingAllIgnoreCase(search, search, search, pageable);
        return users.map(user -> modelMapper.map(user, UserDto.class));
    }



    @Override
    public UserDto getUserById(String userId) {
        Optional<User> UserOp = userRepository.findById(userId);
        if (!UserOp.isPresent())
            throw new NotFoundException("Cant find User!");
        return modelMapper.map(UserOp.get(), UserDto.class);
    }


    @Override
    public Boolean checkExistByUsername(String username) {
        {
            return userRepository.existsByUserName(username);
        }
    }



    // Cập nhật lại User (cập nhật lại toàn bộ các thuộc tính)

    @Override
    public UserDto updateUser(Principal principal, UpdateUserDto userDto) {
        User existingUser = userRepository.findByUserName(principal.getName());
        if (existingUser == null) throw new NotFoundException("Unable to update User!");
        // Nếu mã không giống mã cũ thì kiểm tra mã mới đã tồn tại trong database hay chưa
        if (!existingUser.getEmail().equals(userDto.getEmail())
                && userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateKeyException(String.format("Email đã %s đã tồn tại", userDto.getEmail()));
        }
//        modelMapper.map(userDto, existingUser);
        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPhoneNumber(userDto.getPhoneNumber());
        existingUser.setAvatar(userDto.getAvatar());
        existingUser.setUpdateAt(new Date(new java.util.Date().getTime()));
        return modelMapper.map(userRepository.save(existingUser), UserDto.class);
    }


    @Override
    public void changeStatusUser(String userId) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (!existingUser.isPresent()) throw new NotFoundException("Unable to dalete User!");

        existingUser.get().setIsActive(!existingUser.get().getIsActive());
        existingUser.get().setUpdateAt(new Date(new java.util.Date().getTime()));
        userRepository.save(existingUser.get());
    }



    @Override
    public UserDto getUserByUserName(String userName) {
        User user = userRepository.findByUserName(userName);
        if (user == null) throw new NotFoundException("Wrong username or password !!");
        if (!user.getIsActive()) throw new NotFoundException("User has been deleted !");
        return modelMapper.map(user, UserDto.class);
    }


    @Override
    public void deleteUser(String userId) {
        Optional<User> existingUser = userRepository.findById(userId);
        if (!existingUser.isPresent()) throw new NotFoundException("Unable to dalete User!");
        userRepository.deleteById(userId);
    }


    @Override
    public void changePassword(ChangePasswordDto userDto, Principal principal) {
        User user = userRepository.findByUserName(principal.getName());
        if (!user.getPassword().trim().equals(userDto.getOldPassword()))
            throw new InvalidException("Password Invalid");
        user.setPassword(userDto.getNewPassword());
        userRepository.save(user);
    }

    @Override
    public UserDto getMyInf(Principal principal){
        return modelMapper.map(userRepository.findByUserName(principal.getName()), UserDto.class);
    }


    @Override
    public boolean resetPassword(String username, String newPass) {
        User existingUser = userRepository.findByUserName(username);
        if (existingUser==null) throw new NotFoundException("User not found!");
        existingUser.setPassword(newPass);
        userRepository.save(existingUser);
        return true;
    }
    @Override
    public String getResetPasswordCode(String username){
        User existingUser = userRepository.findByUserName(username);
        if (existingUser==null) throw new NotFoundException("User not found!");
        Random random = new Random();
        String resetCode = String.format("%04d", random.nextInt(10000));
        iMailService.sendCodeForgetPassword(existingUser.getEmail(), resetCode);
        return resetCode;
    }
}
