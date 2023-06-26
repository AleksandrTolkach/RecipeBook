package by.toukach.recipebook.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

/**
 * The object represents refresh token information.
 */
@Data
@Builder
public class RefreshTokenDto {
  private Long id;
  private UserDto userDto;
  private String token;
  private Instant expiryDate;
}
