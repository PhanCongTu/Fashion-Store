package personal.tu.fashionstore.securities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import personal.tu.fashionstore.entities.User;
import personal.tu.fashionstore.repositories.UserRepository;

import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public JwtUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user==null)
            throw new UsernameNotFoundException(String.format("Tài khoản có username %s không tồn tại", username));
        return getUserDetails(user);
    }

    private JwtUserDetails getUserDetails(User user) {
        return new JwtUserDetails(
                user.getName(),
                user.getUserName(),
                user.getPassword(),
                user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()),
                user.getIsActive()
        );
    }
}
