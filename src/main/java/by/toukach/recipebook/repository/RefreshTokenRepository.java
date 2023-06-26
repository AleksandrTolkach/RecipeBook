package by.toukach.recipebook.repository;

import by.toukach.recipebook.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing and storing refresh tokens.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  /**
   * Finds refresh token by its data.
   *
   * @param token refresh token
   * @return an Optional containing refresh token if it exists, or an empty Optional if id does not
   */
  Optional<RefreshToken> findByToken(String token);

  /**
   * Finds refresh token by user id.
   *
   * @param userId The id of user to find
   * @return an Optional containing refresh token if it exists, or an empty Optional if id does not
   */
  Optional<RefreshToken> findByUserId(Long userId);

  /**
   * Deletes refresh token by user entity id.
   *
   * @param id The id of user to find
   */
  void deleteRefreshTokenByUserId(Long id);
}
