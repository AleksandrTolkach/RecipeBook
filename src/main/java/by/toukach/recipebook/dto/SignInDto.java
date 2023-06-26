package by.toukach.recipebook.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Data transfer object that transfer cookies and user information.
 */
@Data
@Builder
public class SignInDto {
  private String accessTokenCookie;
  private String refreshTokenCookie;
  private UserDto user;
}
