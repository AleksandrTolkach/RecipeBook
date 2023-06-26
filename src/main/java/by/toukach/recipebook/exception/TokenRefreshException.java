package by.toukach.recipebook.exception;

/**
 * Thrown to indicate that an access token refresh operation failed.
 */
public class TokenRefreshException extends RuntimeException {

  public TokenRefreshException(String message) {
    super(message);
  }
}
