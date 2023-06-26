package by.toukach.recipebook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A data transfer object that represents a request to update a User entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatingDto {
  @NotBlank
  @Size(max = 50)
  private String login;
  @NotBlank
  @Size(max = 50)
  private String firstName;
  @NotBlank
  @Size(max = 50)
  private String lastName;
}
