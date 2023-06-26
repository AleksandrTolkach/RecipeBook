package by.toukach.recipebook.controller;

import by.toukach.recipebook.dto.PagedResponse;
import by.toukach.recipebook.dto.RecipeCreatingDto;
import by.toukach.recipebook.dto.RecipeDetailsDto;
import by.toukach.recipebook.dto.RecipeDto;
import by.toukach.recipebook.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * A controller class for Recipe entity.
 */
@Slf4j
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {
  private final RecipeService recipeService;

  /**
   * This method is responsible for creating a recipe.
   *
   * @param recipeCreatingDto the data of the recipe to create
   * @param recipePhoto a photo for the recipe
   * @param stepPhotos photos for steps
   * @return created recipe
   */
  @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('USER')")
  public RecipeDetailsDto createRecipe(@RequestPart @Valid RecipeCreatingDto recipeCreatingDto,
      @RequestPart MultipartFile recipePhoto,
      @RequestPart MultipartFile[] stepPhotos) {
    log.info("POST-request: create recipe");
    return recipeService.createRecipe(recipeCreatingDto, recipePhoto, stepPhotos);
  }

  /**
   * This method is responsible for adding/removing a recipe to/from favorites.
   *
   * @param id The id of the recipe
   * @return added/removed from favorites recipe
   */
  @PutMapping("/favorites/{id}")
  @PreAuthorize("hasRole('USER')")
  public RecipeDetailsDto toggleFavoriteRecipe(@PathVariable Long id) {
    log.info("PUT-mapping: toggle favorite recipe");
    return recipeService.toggleFavoriteRecipe(id);
  }

  /**
   * This method is responsible for finding recipe by id.
   *
   * @param id The id of the recipe
   * @return the recipe with specified id
   */
  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('USER')")
  public RecipeDetailsDto findRecipe(@PathVariable Long id) {
    log.info(String.format("GET-request: find recipe with id: %s", id));
    return recipeService.findRecipe(id);
  }

  /**
   * This method is responsible for finding page with recipes.
   *
   * @param page page number
   * @param size page size
   * @return the specified page with recipes
   */
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('USER')")
  public PagedResponse<RecipeDto> findRecipes(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size) {
    log.info("GET-request: find recipes");
    return recipeService.findRecipes(page, size);
  }

  /**
   * This method is responsible for finding page with recipes by author id.
   *
   * @param id the id of the author
   * @param page page number
   * @param size page size
   * @return the specified page with recipes
   */
  @GetMapping("/user/{id}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('USER')")
  public PagedResponse<RecipeDto> findRecipesByUser(@PathVariable Long id,
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size) {
    log.info(String.format("GET-request: find recipes by user id: %s", id));
    return recipeService.findRecipesByUserId(id, page, size);
  }

  /**
   * This method is responsible for finding page with saved recipes.
   *
   * @param page page number
   * @param size page size
   * @return the specified page with saved recipes
   */
  @GetMapping("/favorites")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('USER')")
  public PagedResponse<RecipeDto> findSavedRecipes(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size) {
    log.info("GET-request: find saved recipes");
    return recipeService.findSavedRecipes(page, size);
  }

  /**
   * This method is responsible for updating the recipe.
   *
   * @param id the id of the recipe
   * @param recipeCreatingDto the data of the recipe to update
   * @param recipePhoto a photo for the recipe
   * @param stepPhotos photos for steps
   * @return updated recipe
   */
  @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('USER')")
  public RecipeDetailsDto updateRecipe(@PathVariable Long id,
      @RequestPart @Valid RecipeCreatingDto recipeCreatingDto,
      @RequestPart MultipartFile recipePhoto,
      @RequestPart MultipartFile[] stepPhotos) {
    log.info(String.format("PUT-request: update recipe with id: %s", id));
    return recipeService.updateRecipe(id, recipeCreatingDto, recipePhoto, stepPhotos);
  }

  /**
   * This method is responsible for deleting the recipe.
   *
   * @param id the id of the recipe
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('USER')")
  public void deleteRecipe(@PathVariable Long id) {
    log.info(String.format("DELETE-request: delete recipe with id: %s", id));
    recipeService.deleteRecipe(id);
  }
}
