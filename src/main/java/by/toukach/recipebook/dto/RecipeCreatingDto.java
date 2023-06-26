package by.toukach.recipebook.dto;

import by.toukach.recipebook.utils.Constraints;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * A dto class for creating a new recipe.
 */
@Data
@Builder
@AllArgsConstructor
public class RecipeCreatingDto {
  @NotBlank
  @Size(min = 1, max = 75)
  @Pattern(regexp = Constraints.RECIPE_NAME_REGEX,
      message = Constraints.RECIPE_NAME_MESSAGE)
  private String recipeName;
  @NotNull
  private List<@NotBlank @Size(min = 5, max = 700) String> steps;
  @NotNull
  private List<@Valid IngredientCreatingDto> ingredients;
  @NotNull
  @Positive
  private Integer cookTime;
}
