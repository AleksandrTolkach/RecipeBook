package by.toukach.recipebook.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

/**
 * This interface is responsible for handling intermediate operations on tokens.
 */
public interface TokenService {

  /**
   * Generates cookie with access token.
   *
   * @param username of the user being processed
   * @return access cookie
   */
  ResponseCookie generateAccessJwtCookie(String username);

  /**
   * Generates refresh cookie.
   *
   * @param refreshToken refresh token data
   * @return refresh cookie
   */
  ResponseCookie generateRefreshJwtCookie(String refreshToken);

  /**
   * Gets access token from cookie.
   *
   * @param request http request
   * @return token's data
   */
  String getJwtAccessFromCookies(HttpServletRequest request);

  /**
   * Gets refresh token from cookie.
   *
   * @param request http request
   * @return token's data
   */
  String getJwtRefreshFromCookies(HttpServletRequest request);

  /**
   * Gets cookie with empty access token.
   *
   * @return cookie with empty value
   */
  ResponseCookie getCleanJwtAccessCookie();

  /**
   * Gets cookie with empty refresh token.
   *
   * @return cookie with empty value
   */
  ResponseCookie getCleanJwtRefreshCookie();

  /**
   * Gets username from token.
   *
   * @param token token
   * @return username
   */
  String getUsernameFromJwtToken(String token);

  /**
   * Validates token.
   *
   * @param token token's data
   * @return true if token is valid and false if not
   */
  boolean validateJwtToken(String token);

  /**
   * Generates access token from username.
   *
   * @param username username
   * @return token
   */
  String generateTokenFromUsername(String username);

  /**
   * Updates user's login and password in SecurityContext.
   *
   * @param username The username of authenticated user
   */
  void updateUserCredentialsInContext(String username);
}
