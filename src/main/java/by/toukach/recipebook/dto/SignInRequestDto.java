package by.toukach.recipebook.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * Data transfer object that represents request to sign in user.
 */
@Data
@Builder
public class SignInRequestDto {
  @NotBlank
  private String login;
  @NotBlank
  private String password;
}
