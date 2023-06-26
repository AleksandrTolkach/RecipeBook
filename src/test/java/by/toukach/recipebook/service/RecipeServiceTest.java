package by.toukach.recipebook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.toukach.recipebook.BaseTest;
import by.toukach.recipebook.dto.PagedResponse;
import by.toukach.recipebook.dto.RecipeCreatingDto;
import by.toukach.recipebook.dto.RecipeDetailsDto;
import by.toukach.recipebook.dto.RecipeDto;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.entity.Recipe;
import by.toukach.recipebook.exception.EntityNotFoundException;
import by.toukach.recipebook.exception.OwnerException;
import by.toukach.recipebook.repository.RecipeRepository;
import by.toukach.recipebook.service.impl.RecipeServiceImpl;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RecipeServiceTest extends BaseTest {
  @InjectMocks
  private RecipeServiceImpl recipeService;
  @Mock
  private RecipeRepository recipeRepository;
  @Mock
  private UserService userService;
  @Mock
  private SecurityContext securityContext;
  @Mock
  private Authentication authentication;
  @Mock
  private S3Service s3Service;
  private UserDto userDto;
  private Recipe existingRecipe;
  private Recipe savedRecipe;
  private RecipeDetailsDto recipeDetailsDto;
  private RecipeCreatingDto recipeCreatingDto;
  private PagedResponse<RecipeDto> recipeDtoPages;
  private Page<Recipe> recipePages;
  private PageRequest pageRequest;
  private MultipartFile photoMultipartFile;

  @BeforeEach
  public void init() throws IOException {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(createUserDetails());

    userDto = createExistingUserDto();
    existingRecipe = createExistingRecipe();
    savedRecipe = createSavedRecipe();
    recipeDetailsDto = createRecipeDetailsDto();
    recipeCreatingDto = createRecipeCreatingDto();
    recipeDtoPages = createRecipePagedResponse();
    recipePages = createRecipePage();
    pageRequest = PageRequest.of(page, size);
    photoMultipartFile = createPhotoMultipartFile();
  }

  @Test
  void createRecipeTest_should_CreateRecipe() throws IOException {
    when(userService.findUser(id)).thenReturn(userDto);
    when(s3Service.putObject(photoMultipartFile)).thenReturn(photoLink);
    when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);
    when(s3Service.getObjectUrl(any())).thenReturn(photoLink);

    RecipeDetailsDto expectedResult = recipeDetailsDto;

    RecipeDetailsDto actualResult = recipeService
        .createRecipe(recipeCreatingDto, photoMultipartFile,
            new MultipartFile[] {photoMultipartFile});

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void createRecipeTest_should_ThrowError_WhenUnAuthorizedUserCreatesRecipe() {
    when(userService.findUser(id)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class,
        () -> recipeService.createRecipe(recipeCreatingDto, photoMultipartFile,
            new MultipartFile[] {photoMultipartFile}));
  }

  @Test
  void toggleFavoriteRecipeTest_should_ToggleFavoriteRecipe() {
    when(userService.findUser(id)).thenReturn(userDto);
    when(recipeRepository.findById(id)).thenReturn(Optional.of(savedRecipe));
    when(s3Service.getObjectUrl(any())).thenReturn(photoLink);

    RecipeDetailsDto expectedResult = recipeDetailsDto;
    RecipeDetailsDto actualResult = recipeService.toggleFavoriteRecipe(id);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void toggleFavoriteRecipeTest_should_ThrowError_WhenToggleUnExistingRecipe() {
    when(userService.findUser(id)).thenReturn(userDto);
    when(recipeRepository.findById(id)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class, () -> recipeService.toggleFavoriteRecipe(id));
  }

  @Test
  void findRecipeTest_should_FindRecipe() {
    when(recipeRepository.findById(id)).thenReturn(Optional.of(existingRecipe));
    when(s3Service.getObjectUrl(any())).thenReturn(photoLink);

    RecipeDetailsDto expectedResult = recipeDetailsDto;
    RecipeDetailsDto actualResult = recipeService.findRecipe(id);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void findRecipeTest_should_ThrowError_WhenSearchingUnExistingRecipe() {
    when(recipeRepository.findById(id)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class, () -> recipeService.findRecipe(id));
  }

  @Test
  void findRecipesTest_should_FindRecipes() {
    when(recipeRepository.findAllByOrderByUpdatedAtDesc(pageRequest))
        .thenReturn(recipePages);
    when(s3Service.getObjectUrl(any())).thenReturn(photoLink);

    PagedResponse<RecipeDto> expectedResult = recipeDtoPages;
    PagedResponse<RecipeDto> actualResult = recipeService.findRecipes(page, size);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void findRecipesByUserIdTest_should_FindRecipes() {
    when(userService.findUser(id)).thenReturn(userDto);
    when(recipeRepository.findAllByAuthorIdOrderByUpdatedAtDesc(id, pageRequest))
        .thenReturn(recipePages);
    when(s3Service.getObjectUrl(any())).thenReturn(photoLink);

    PagedResponse<RecipeDto> expectedResult = recipeDtoPages;
    PagedResponse<RecipeDto> actualResult = recipeService.findRecipesByUserId(id, page, size);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void findRecipesByUserIdTest_should_ThrowError_WhenSearchingByUnExistingUserId() {
    when(userService.findUser(id)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class,
        () -> recipeService.findRecipesByUserId(id, page, size));
  }

  @Test
  void findSavedRecipesTest_should_FindSavedRecipes() {
    when(recipeRepository.findAllBySavesIdOrderByUpdatedAtDesc(id, pageRequest))
        .thenReturn(recipePages);
    when(s3Service.getObjectUrl(any())).thenReturn(photoLink);

    PagedResponse<RecipeDto> expectedResult = recipeDtoPages;
    PagedResponse<RecipeDto> actualResult = recipeService.findSavedRecipes(page, size);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void updateRecipeTest_should_UpdateRecipe() throws IOException {
    when(recipeRepository.findById(id)).thenReturn(Optional.of(existingRecipe));
    when(userService.findUser(id)).thenReturn(userDto);
    when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);
    when(s3Service.putObject(photoMultipartFile)).thenReturn(photoName);
    when(s3Service.getObjectUrl(any())).thenReturn(photoLink);

    RecipeDetailsDto expectedResult = recipeDetailsDto;
    RecipeDetailsDto actualResult = recipeService
        .updateRecipe(id, recipeCreatingDto, photoMultipartFile,
            new MultipartFile[] {photoMultipartFile});

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void updateRecipeTest_should_ThrowError_WhenUnExistingUserUpdateRecipe() {
    when(recipeRepository.findById(id)).thenReturn(Optional.of(existingRecipe));
    when(userService.findUser(id)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class,
        () -> recipeService.updateRecipe(id, recipeCreatingDto, photoMultipartFile,
            new MultipartFile[] {photoMultipartFile}));
  }

  @Test
  void updateRecipeTest_should_ThrowError_WhenUpdatingUnExistingRecipe() {
    when(recipeRepository.findById(id)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class,
        () -> recipeService.updateRecipe(id, recipeCreatingDto, photoMultipartFile,
            new MultipartFile[] {photoMultipartFile}));
  }

  @Test
  void updateRecipeTest_should_ThrowError_WhenUpdatingSomeoneElseRecipe() {
    userDto.setId(unExistingId);
    when(recipeRepository.findById(id)).thenReturn(Optional.of(existingRecipe));
    when(userService.findUser(id)).thenReturn(userDto);

    assertThrows(OwnerException.class, () -> recipeService
        .updateRecipe(id, recipeCreatingDto, photoMultipartFile,
            new MultipartFile[] {photoMultipartFile}));
  }

  @Test
  void deleteRecipeTest_should_DeleteRecipe() {
    when(recipeRepository.findById(id)).thenReturn(Optional.of(existingRecipe));
    when(userService.findUser(id)).thenReturn(userDto);

    recipeService.deleteRecipe(id);

    verify(recipeRepository, times(1)).deleteById(id);
  }

  @Test
  void deleteRecipeTest_should_ThrowError_WhenUnExistingUserDeleteRecipe() {
    when(recipeRepository.findById(id)).thenReturn(Optional.of(existingRecipe));
    when(userService.findUser(id)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class,
        () -> recipeService.updateRecipe(id, recipeCreatingDto, photoMultipartFile,
            new MultipartFile[] {photoMultipartFile}));
  }

  @Test
  void deleteRecipeTest_should_ThrowError_WhenDeletingUnExistingRecipe() {
    when(recipeRepository.findById(id)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class, () -> recipeService.deleteRecipe(id));
  }

  @Test
  void deleteRecipeTest_should_ThrowError_WhenDeletingSomeoneElseRecipe() {
    userDto.setId(unExistingId);
    when(recipeRepository.findById(id)).thenReturn(Optional.of(existingRecipe));
    when(userService.findUser(id)).thenReturn(userDto);

    assertThrows(OwnerException.class,
        () -> recipeService.updateRecipe(id, recipeCreatingDto, photoMultipartFile,
            new MultipartFile[] {photoMultipartFile}));
  }
}
