package personal.tu.fashionstore.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import personal.tu.fashionstore.dtos.Login;
import personal.tu.fashionstore.dtos.SignUp;
import personal.tu.fashionstore.dtos.TokenDetails;
import personal.tu.fashionstore.dtos.User.UserDto;
import personal.tu.fashionstore.exceptions.InvalidException;
import personal.tu.fashionstore.exceptions.UserNotFoundAuthenticationException;
import personal.tu.fashionstore.securities.CustomUserDetailsService;
import personal.tu.fashionstore.securities.JwtTokenUtils;
import personal.tu.fashionstore.securities.JwtUserDetails;
import personal.tu.fashionstore.securities.UserAuthenticationToken;
import personal.tu.fashionstore.services.User.IUserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api")
public class AuthenticationController {
    @Autowired
    IUserService iUserService;
    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtTokenUtils jwtTokenUtils;
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthenticationController(AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService,
                                    JwtTokenUtils jwtTokenUtils) {
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    /***
     * @Authorize: Bất cứ ai cũng có thể đăng ký tài khoản
     * @param signUp : Các thông tin cẩn thiết cho 1 tài khoản mới
     * @return: Thông tin User mới được tạo
     *
     *  User mới sẽ mặc định chỉ có quyền USER
     *  Muốn của quyền ADMIN
     */

    @PostMapping("/signup")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid SignUp signUp) {
        return new ResponseEntity<>(iUserService.createUser(signUp), HttpStatus.CREATED);
    }

    /***
     *
     * @param dto : Dto chứa username và password để đăng nhập
     * @return: Một về 1 Dto chứa token để sử dụng cho các Request sau và 1 số thông tin khác
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDetails> login(@Valid @RequestBody Login dto) {
        UserDto userDto = iUserService.getUserByUserNameAndPassword(dto.getUsername(), dto.getPassword());
        if (!userDto.getIsActive())
            throw new UserNotFoundAuthenticationException("User has been unactivated");
        UserAuthenticationToken authenticationToken = new UserAuthenticationToken(
                dto.getUsername(),
                dto.getPassword(),
                true
        );
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (UserNotFoundAuthenticationException | BadCredentialsException ex) {
            throw new InvalidException(ex.getMessage());
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        final JwtUserDetails userDetails = customUserDetailsService
                .loadUserByUsername(dto.getUsername());
        final TokenDetails result = jwtTokenUtils.getTokenDetails(userDetails, null);
        log.info(String.format("User %s login at %s", dto.getUsername(), new Date()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /***
     * @Authorize: ADMIN (Chỉ admin mới có quyền thêm role ADMIN cho User)
     * @param userId: ID của user được thêm Role
     * @return: Thông báo thành công
     */

    @PostMapping("/upgrade/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> upgradeRole(@PathVariable String  userId) {
        iUserService.upgradeRole(userId);
        return new ResponseEntity<>(String.format("User with ID %s has been upgraded to admin", userId), HttpStatus.OK);
    }

    /***
     *  Dùng để test user
     * @param principal
     * @return
     */
    @GetMapping("/hello-user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> sayHelloUser(Principal principal) {
        return new ResponseEntity<>(String.format("Hello USER %s", principal.getName()), HttpStatus.OK);
    }
    /***
     *  Dùng để test admin
     * @param principal
     * @return
     */
    @GetMapping("/hello-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sayHelloAdmin(Principal principal) {
        return new ResponseEntity<>(String.format("Hello ADMIN %s", principal.getName()), HttpStatus.OK);
    }

}
