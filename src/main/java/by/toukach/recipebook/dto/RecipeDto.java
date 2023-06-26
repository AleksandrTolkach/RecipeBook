package by.toukach.recipebook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

/**
 * A class representing DTO for short information about recipe.
 */
@Data
@Builder
@AllArgsConstructor
public class RecipeDto {
  private Long id;
  private String recipeName;
  @Default
  private String recipePhotoLink = "none";
  private boolean saved;
}
