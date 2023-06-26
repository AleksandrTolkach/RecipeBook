package by.toukach.recipebook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * A class representing DTO for step.
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class StepDto extends BaseDto {
  private String description;
  @Default
  private String stepPhotoLink = "none";
}
