package personal.tu.fashionstore.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import personal.tu.fashionstore.entities.User;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUserNameAndPassword(String userName, String password);
    User findByUserName(String userName);
    Boolean existsByUserName(String userName);
    Boolean existsByEmail(String email);
    Page<User> findByNameContainingOrEmailContainingOrPhoneNumberContainingAllIgnoreCase(String name, String email, String Phone, Pageable pageable);
}
