package by.toukach.recipebook.dto;

import by.toukach.recipebook.utils.Constraints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data transfer object that represents a request to create a new User entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreatingDto {
  @NotBlank
  @Size(max = 50)
  private String login;
  @NotBlank
  @Size(max = 50)
  private String firstName;
  @NotBlank
  @Size(max = 50)
  private String lastName;
  @NotBlank
  @Size(max = 50)
  @Pattern(regexp = Constraints.PASSWORD_REGEX,
      message = Constraints.PASSWORD_MESSAGE)
  private String password;
}
