package by.toukach.recipebook.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * A class representing DTO for recipe.
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class RecipeDetailsDto extends BaseDto {
  private String recipeName;
  private UserDto author;
  private List<StepDto> recipeSteps;
  private List<IngredientDto> ingredients;
  private int cookTime;
  @Default
  private String recipePhotoLink = "none";
  private boolean saved;
}
