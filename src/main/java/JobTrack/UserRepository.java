package JobTrack;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findByUsername(String username);

    public Optional<User> findById(int id);

    public Optional<User> findByEmail(String email);

    public boolean existsByRole(Role role);

    @Query("SELECT u FROM User u")
    public Page<User> findAllUsers(Pageable pageable);  
}
