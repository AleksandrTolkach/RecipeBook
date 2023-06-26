package by.toukach.recipebook.service.impl;

import by.toukach.recipebook.converter.IngredientConverter;
import by.toukach.recipebook.converter.PagedResponseConverter;
import by.toukach.recipebook.converter.RecipeConverter;
import by.toukach.recipebook.converter.StepConverter;
import by.toukach.recipebook.converter.UserConverter;
import by.toukach.recipebook.dto.PagedResponse;
import by.toukach.recipebook.dto.RecipeCreatingDto;
import by.toukach.recipebook.dto.RecipeDetailsDto;
import by.toukach.recipebook.dto.RecipeDto;
import by.toukach.recipebook.dto.StepDto;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.entity.Ingredient;
import by.toukach.recipebook.entity.Recipe;
import by.toukach.recipebook.entity.Step;
import by.toukach.recipebook.entity.User;
import by.toukach.recipebook.entity.UserDetailsImpl;
import by.toukach.recipebook.exception.EntityNotFoundException;
import by.toukach.recipebook.exception.ExceptionMessage;
import by.toukach.recipebook.exception.InvalidArgumentValueException;
import by.toukach.recipebook.exception.OwnerException;
import by.toukach.recipebook.repository.RecipeRepository;
import by.toukach.recipebook.service.RecipeService;
import by.toukach.recipebook.service.S3Service;
import by.toukach.recipebook.service.UserService;
import by.toukach.recipebook.utils.SecurityUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * A class for managing recipes.
 */
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

  private final RecipeRepository recipeRepository;
  private final UserService userService;
  private final S3Service s3Service;

  @Transactional
  @Override
  public RecipeDetailsDto createRecipe(RecipeCreatingDto recipeCreatingDto,
      MultipartFile recipePhoto, MultipartFile[] stepPhotos) {
    User user = findCurrentUser();

    Recipe recipe = RecipeConverter.convertToEntity(recipeCreatingDto);
    recipe.setAuthor(user);
    addIngredientsAndSteps(recipe, recipeCreatingDto, stepPhotos);

    String recipePhotoName = s3Service.putObject(recipePhoto);
    recipe.setRecipePhotoName(recipePhotoName);

    recipe = recipeRepository.save(recipe);
    return getRecipeDetailsDto(recipe);
  }

  @Transactional
  @Override
  public RecipeDetailsDto toggleFavoriteRecipe(Long id) {
    User user = findCurrentUser();
    Recipe recipe = findRecipeByIdIfExists(id);

    recipe.toggleSave(user);

    return getRecipeDetailsDto(recipe);
  }

  @Override
  public RecipeDetailsDto findRecipe(Long id) {
    Recipe recipe = findRecipeByIdIfExists(id);

    List<String> stepPhotoLinks = recipe.getSteps().stream()
        .map(step -> s3Service.getObjectUrl(step.getPhotoName()))
        .toList();

    RecipeDetailsDto recipeDetailsDto = RecipeConverter.convertToDetailsDto(recipe);
    List<StepDto> recipeSteps = recipeDetailsDto.getRecipeSteps();

    for (int i = 0; i < recipeSteps.size(); i++) {
      recipeSteps.get(i).setStepPhotoLink(stepPhotoLinks.get(i));
    }

    return getRecipeDetailsDto(recipe);
  }

  @Override
  public PagedResponse<RecipeDto> findRecipes(int page, int size) {
    Page<Recipe> recipePage =
        recipeRepository.findAllByOrderByUpdatedAtDesc(PageRequest.of(page, size));

    return getRecipeDtoPagedResponse(recipePage);
  }

  @Override
  public PagedResponse<RecipeDto> findRecipesByUserId(Long id, int page, int size) {
    userService.findUser(id);
    Page<Recipe> recipePage = recipeRepository.findAllByAuthorIdOrderByUpdatedAtDesc(
        id, PageRequest.of(page, size));

    return getRecipeDtoPagedResponse(recipePage);
  }

  @Override
  public PagedResponse<RecipeDto> findSavedRecipes(int page, int size) {
    UserDetailsImpl userDetails = SecurityUtil.getUserDetails();

    Page<Recipe> recipePage = recipeRepository.findAllBySavesIdOrderByUpdatedAtDesc(
        userDetails.getId(), PageRequest.of(page, size));

    return getRecipeDtoPagedResponse(recipePage);
  }

  @Transactional
  @Override
  public RecipeDetailsDto updateRecipe(Long id, RecipeCreatingDto recipeCreatingDto,
      MultipartFile recipePhoto, MultipartFile[] stepPhotos) {
    Recipe recipe = findRecipeByIdIfExists(id);

    checkOwner(recipe.getAuthor().getId());
    updateRecipeFields(recipe, recipeCreatingDto, stepPhotos);

    recipe = recipeRepository.save(recipe);
    return getRecipeDetailsDto(recipe);
  }

  @Transactional
  @Override
  public void deleteRecipe(Long id) {
    Recipe recipe = findRecipeByIdIfExists(id);
    User author = recipe.getAuthor();

    checkOwner(author.getId());

    recipeRepository.deleteById(id);
  }

  private RecipeDetailsDto getRecipeDetailsDto(Recipe recipe) {
    String photoLink = s3Service.getObjectUrl(recipe.getRecipePhotoName());

    List<StepDto> stepDtos = recipe.getSteps().stream()
        .map(step -> {
          StepDto stepDto = StepConverter.convertToDto(step);
          String stepPhotoName = step.getPhotoName();
          String stepPhotoLink = s3Service.getObjectUrl(stepPhotoName);
          stepDto.setStepPhotoLink(stepPhotoLink);
          return stepDto;
        })
        .toList();

    RecipeDetailsDto recipeDetailsDto = RecipeConverter.convertToDetailsDto(recipe);
    recipeDetailsDto.setRecipePhotoLink(photoLink);
    recipeDetailsDto.setRecipeSteps(stepDtos);

    String userPhotoName = recipe.getAuthor().getUserPhotoName();
    if (userPhotoName != null && !userPhotoName.isEmpty()) {
      String userPhotoLink = s3Service.getObjectUrl(userPhotoName);
      recipeDetailsDto.getAuthor().setUserPhotoLink(userPhotoLink);
    }
    return recipeDetailsDto;
  }

  private void updateRecipeFields(Recipe recipe, RecipeCreatingDto recipeCreatingDto,
      MultipartFile[] stepPhotos) {
    recipe.removeAllSteps();
    recipe.removeAllIngredients();
    addIngredientsAndSteps(recipe, recipeCreatingDto, stepPhotos);
    recipe.setRecipeName(recipeCreatingDto.getRecipeName());
    recipe.setCookTime(recipeCreatingDto.getCookTime());
    recipe.setUpdatedAt(LocalDateTime.now());
  }

  private void checkOwner(Long authorId) {
    User user = findCurrentUser();

    if (!user.getId().equals(authorId)) {
      throw new OwnerException(ExceptionMessage.OWNER_CONFLICT);
    }
  }

  private void addIngredientsAndSteps(Recipe recipe, RecipeCreatingDto recipeCreatingDto,
      MultipartFile[] stepPhotos) {
    List<String> stepDescriptions = recipeCreatingDto.getSteps();

    if (stepPhotos.length != stepDescriptions.size()) {
      throw new InvalidArgumentValueException(ExceptionMessage.STEP_PHOTO_MISMATCH_COUNT);
    }

    List<Step> steps = new ArrayList<>();
    for (int i = 0; i < stepDescriptions.size(); i++) {
      String stepPhotoName = s3Service.putObject(stepPhotos[i]);
      steps.add(new Step(stepDescriptions.get(i), stepPhotoName));
    }
    List<Ingredient> ingredients = recipeCreatingDto.getIngredients().stream()
        .map(IngredientConverter::convertToEntity)
        .toList();

    recipe.addSteps(steps);
    recipe.addIngredients(ingredients);
  }

  private Recipe findRecipeByIdIfExists(Long id) {
    return recipeRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException(String.format(ExceptionMessage.RECIPE_NOT_FOUND, id)));
  }

  private User findCurrentUser() {
    UserDetailsImpl userDetails = SecurityUtil.getUserDetails();
    UserDto userDto = userService.findUser(userDetails.getId());
    User user = UserConverter.convertToEntity(userDto);
    user.setUserPhotoName(userDetails.getUserPhotoName());
    return user;
  }

  private PagedResponse<RecipeDto> getRecipeDtoPagedResponse(Page<Recipe> recipePage) {
    List<RecipeDto> content = recipePage.getContent().stream()
        .map(recipe -> {
          String recipePhotoLink = s3Service.getObjectUrl(recipe.getRecipePhotoName());
          RecipeDto recipeDto = RecipeConverter.convertToDto(recipe);
          recipeDto.setRecipePhotoLink(recipePhotoLink);
          return recipeDto;
        })
        .toList();

    return PagedResponseConverter.convertToResponse(recipePage, content);
  }
}
