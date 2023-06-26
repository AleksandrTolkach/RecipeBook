package by.toukach.recipebook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * A dto class for creating a new ingredient in recipe.
 */
@Data
@Builder
@AllArgsConstructor
public class IngredientCreatingDto {
  @NotBlank
  @Size(min = 1, max = 100)
  private String ingredientName;
  @NotBlank
  private String measureUnit;
  @NotNull
  @Positive
  private Integer quantity;
}
