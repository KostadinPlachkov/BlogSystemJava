package Blog.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import Blog.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}