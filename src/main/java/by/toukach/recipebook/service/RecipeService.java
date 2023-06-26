package by.toukach.recipebook.service;

import by.toukach.recipebook.dto.PagedResponse;
import by.toukach.recipebook.dto.RecipeCreatingDto;
import by.toukach.recipebook.dto.RecipeDetailsDto;
import by.toukach.recipebook.dto.RecipeDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * An interface for managing recipes.
 */
public interface RecipeService {

  /**
   * This method is responsible for creating a recipe.
   *
   * @param recipeCreatingDto the data of the recipe to create
   * @param recipePhoto a photo for the recipe
   * @param stepPhotos photos for steps
   * @return created recipe
   */
  RecipeDetailsDto createRecipe(RecipeCreatingDto recipeCreatingDto,
      MultipartFile recipePhoto, MultipartFile[] stepPhotos);

  /**
   * This method is responsible for add/delete recipes to/from favorites.
   *
   * @param id the id of a specific recipe
   * @return information about recipe
   */
  RecipeDetailsDto toggleFavoriteRecipe(Long id);

  /**
   * This method is responsible for finding a recipe by id.
   *
   * @param id the id of a specific recipe
   * @return information about recipe
   */
  RecipeDetailsDto findRecipe(Long id);

  /**
   * This method responsible for finding page with recipes.
   *
   * @param page page number
   * @param size page size
   * @return the specific page with recipes
   */
  PagedResponse<RecipeDto> findRecipes(int page, int size);

  /**
   *  This method is responsible for finding page with recipes by author id.
   *
   * @param id the id of the author
   * @param page page number
   * @param size page size
   * @return the specified page with recipes
   */
  PagedResponse<RecipeDto> findRecipesByUserId(Long id, int page, int size);

  /**
   * This method is responsible for finding page with saved recipes.
   *
   * @param page page number
   * @param size page size
   * @return the specified page with saved recipes
   */
  PagedResponse<RecipeDto> findSavedRecipes(int page, int size);

  /**
   * This method is responsible for updating a recipe.
   *
   * @param recipeCreatingDto the data of the recipe to update
   * @param recipePhoto a photo for the recipe
   * @param stepPhotos photos for steps
   * @return the updated recipe
   */
  RecipeDetailsDto updateRecipe(Long id,
      RecipeCreatingDto recipeCreatingDto, MultipartFile recipePhoto,
      MultipartFile[] stepPhotos);

  /**
   * This method is responsible for deleting the recipe.
   *
   * @param id the id of the recipe
   */
  void deleteRecipe(Long id);
}
