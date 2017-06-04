package Blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Blog.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}