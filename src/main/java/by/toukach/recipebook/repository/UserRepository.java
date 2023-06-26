package by.toukach.recipebook.repository;

import by.toukach.recipebook.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A repository class for User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Returns Optional with User of the user with the specified login.
   *
   * @param login The login of a user
   * @return Optional with User
   */
  Optional<User> findByLogin(String login);

  /**
   * Checks if it exists the entity of the user with the specified login.
   *
   * @param login The login of a user
   * @return true if User entity exists and false if it isn't
   */
  boolean existsByLogin(String login);
}
