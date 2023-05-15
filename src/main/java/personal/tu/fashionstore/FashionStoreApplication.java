package personal.tu.fashionstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import personal.tu.fashionstore.entities.User;
import personal.tu.fashionstore.repositories.UserRepository;
import personal.tu.fashionstore.untils.EnumRole;

import java.util.Arrays;

@SpringBootApplication
@EnableAsync
public class FashionStoreApplication implements CommandLineRunner {
	@Autowired
	private UserRepository userRepository;
	public static void main(String[] args) {
		SpringApplication.run(FashionStoreApplication.class, args);
		System.out.println("-----------------------------------------------------------");
		System.out.println("🚀 Server ready at http://localhost:8383");
	}
	@Override
	public void run(String... args) throws Exception {
		if(userRepository.count()==0){
			User user = new User("User01","user01","123","123123",
					"user01@gmail.com",
					Arrays.asList(EnumRole.ROLE_USER.name()));
			User user1 = new User("Admin01","admin01","123","123123",
					"admin01@gmail.com",
					Arrays.asList(EnumRole.ROLE_ADMIN.name()));
			User user2 = new User("UserAdmin01","useradmin01","123","123123",
					"useradmin01@gmail.com",
					Arrays.asList(EnumRole.ROLE_USER.name(), EnumRole.ROLE_ADMIN.name()));
			userRepository.save(user);
			userRepository.save(user1);
			userRepository.save(user2);
		}
	}
}
