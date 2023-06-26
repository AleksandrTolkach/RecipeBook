package by.toukach.recipebook;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import by.toukach.recipebook.converter.UserConverter;
import by.toukach.recipebook.dto.IngredientCreatingDto;
import by.toukach.recipebook.dto.IngredientDto;
import by.toukach.recipebook.dto.PagedResponse;
import by.toukach.recipebook.dto.RecipeCreatingDto;
import by.toukach.recipebook.dto.RecipeDetailsDto;
import by.toukach.recipebook.dto.RecipeDto;
import by.toukach.recipebook.dto.RefreshTokenDto;
import by.toukach.recipebook.dto.SignInDto;
import by.toukach.recipebook.dto.SignInRequestDto;
import by.toukach.recipebook.dto.StepDto;
import by.toukach.recipebook.dto.UserCreatingDto;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.dto.UserUpdatingDto;
import by.toukach.recipebook.entity.Ingredient;
import by.toukach.recipebook.entity.Recipe;
import by.toukach.recipebook.entity.Step;
import by.toukach.recipebook.entity.User;
import by.toukach.recipebook.entity.UserDetailsImpl;
import by.toukach.recipebook.enumeration.MeasureUnit;
import by.toukach.recipebook.enumeration.Role;
import by.toukach.recipebook.repository.RecipeRepository;
import by.toukach.recipebook.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

public class BaseTest {
  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  protected ObjectMapper objectMapper;
  @Autowired
  protected UserRepository userRepository;
  @Autowired
  protected RecipeRepository recipeRepository;
  protected String login = "user";
  protected String firstName = "joe";
  protected String lastName = "doe";
  protected String password = "1234_Admin";
  protected LocalDateTime localDateTime = LocalDateTime.now();
  protected Long id = 1L;
  protected String existingLogin = "existingLogin";
  protected String accessToken = "accessToken";
  protected String refreshToken = "refreshToken";
  protected String signInPath = "/auth/sign-in";
  protected String signUpPath = "/auth/sign-up";
  protected String refreshPath = "/auth/refresh";
  protected String logoutPath = "/auth/logout";
  protected String usersPath = "/users";
  protected String specifiedUserPath = usersPath + "/%s";
  protected String profilePath = usersPath + "/profile";
  protected String recipesPath = "/recipes";
  protected Long unExistingId = 9999L;
  protected Integer cookTime = 20;
  protected Integer quantity = 1;
  protected MeasureUnit measureUnit = MeasureUnit.PCS;
  protected int page = 0;
  protected int size = 1;
  protected String stepDescription = "someDescription";
  protected String recipeName = "applePie";
  protected String ingredientName = "apple";
  protected String photoLink = "https://link.com";
  protected String photoName = "photo";
  protected String photoPath = "static/images/photo.jpg";

  protected <T> T readValue(String value, TypeReference<T> type) throws Exception {
    return objectMapper.readValue(value, type);
  }

  protected UserDto findExpectedUser(Long userId) {
    Optional<User> user = userRepository.findById(userId);

    assertTrue(user.isPresent());

    return UserConverter.convertToDto(user.get());
  }

  protected UserCreatingDto createUserCreatingDto() {
    return UserCreatingDto.builder()
        .login(login)
        .firstName(firstName)
        .lastName(lastName)
        .password(password)
        .build();
  }

  protected UserDto createUserDto() {
    return UserDto.builder()
        .id(id)
        .createdAt(localDateTime)
        .updatedAt(localDateTime)
        .login(login)
        .firstName(firstName)
        .lastName(lastName)
        .userPhotoLink(null)
        .build();
  }

  protected UserDto createExistingUserDto() {
    return UserDto.builder()
        .id(id)
        .createdAt(localDateTime)
        .updatedAt(localDateTime)
        .login(login)
        .firstName(firstName)
        .lastName(lastName)
        .userPhotoLink(photoLink)
        .build();
  }

  protected User createNewUser() {
    return User.builder()
        .id(id)
        .createdAt(localDateTime)
        .updatedAt(localDateTime)
        .login(login)
        .firstName(firstName)
        .lastName(lastName)
        .password(password)
        .role(Role.USER)
        .build();
  }

  protected User createExistingUser() {
    return User.builder()
        .id(id)
        .createdAt(localDateTime)
        .updatedAt(localDateTime)
        .login(login)
        .firstName(firstName)
        .lastName(lastName)
        .password(password)
        .role(Role.USER)
        .userPhotoName(photoLink)
        .build();
  }

  protected UserUpdatingDto createUserUpdatingDto() {
    return UserUpdatingDto.builder()
        .login(login)
        .firstName(firstName)
        .lastName(lastName)
        .build();
  }

  protected UserDetailsImpl createUserDetails() {
    return new UserDetailsImpl(createNewUser());
  }

  protected SignInDto createSignInDto() {
    return SignInDto.builder()
        .accessTokenCookie(createAccessTokenCookie().toString())
        .refreshTokenCookie(createRefreshTokenCookie().toString())
        .user(createUserDto())
        .build();
  }

  protected ResponseCookie createAccessTokenCookie() {
    return ResponseCookie.from(
            accessToken, accessToken)
        .build();
  }

  protected ResponseCookie createRefreshTokenCookie() {
    return ResponseCookie.from(
            refreshToken, refreshToken)
        .build();
  }

  protected RefreshTokenDto createRefreshTokenDto() {
    return RefreshTokenDto.builder()
        .id(id)
        .userDto(createUserDto())
        .token(refreshToken)
        .expiryDate(Instant.now().plus(1, ChronoUnit.DAYS))
        .build();
  }

  protected SignInDto createSignInDtoAfterLogout() {
    return SignInDto.builder()
        .accessTokenCookie(createAccessTokenCookie().toString())
        .refreshTokenCookie(createRefreshTokenCookie().toString())
        .user(null)
        .build();
  }

  protected SignInRequestDto createSignInRequestDto() {
    return SignInRequestDto.builder()
        .login(login)
        .password(password)
        .build();
  }

  protected MvcResult signIn(String content) throws Exception {
    return mockMvc.perform(post(signInPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
        .andReturn();
  }

  protected Recipe createExistingRecipe() {
    List<User> users = new ArrayList<>();
    users.add(createNewUser());

    Step step = Step.builder()
        .id(id)
        .description(stepDescription)
        .build();
    List<Step> steps = new ArrayList<>();
    steps.add(step);

    Recipe recipe = Recipe.builder()
        .id(id)
        .recipeName(recipeName)
        .author(createExistingUser())
        .steps(steps)
        .cookTime(cookTime)
        .saves(new ArrayList<>())
        .build();

    Ingredient ingredient = createIngredient(recipe);
    List<Ingredient> ingredients = new ArrayList<>();
    ingredients.add(ingredient);

    recipe.setIngredients(ingredients);
    return recipe;
  }

  protected Recipe createSavedRecipe() {
    Recipe recipe = createExistingRecipe();
    recipe.toggleSave(createNewUser());
    return recipe;
  }

  protected Ingredient createIngredient(Recipe recipe) {
    return Ingredient.builder()
        .id(id)
        .ingredientName(ingredientName)
        .quantity(quantity)
        .measureUnit(measureUnit)
        .build();
  }

  protected RecipeDetailsDto createRecipeDetailsDto() {
    StepDto stepDto = StepDto.builder()
        .id(id)
        .description(stepDescription)
        .stepPhotoLink(photoLink)
        .build();

    IngredientDto ingredientDto = IngredientDto.builder()
        .id(id)
        .ingredientName(ingredientName)
        .measureUnit(measureUnit)
        .quantity(quantity)
        .build();

    return RecipeDetailsDto.builder()
        .id(id)
        .recipeName(recipeName)
        .author(createExistingUserDto())
        .recipeSteps(List.of(stepDto))
        .ingredients(List.of(ingredientDto))
        .cookTime(cookTime)
        .recipePhotoLink(photoLink)
        .build();
  }

  protected RecipeCreatingDto createRecipeCreatingDto() {
    IngredientCreatingDto ingredientCreatingDto = IngredientCreatingDto.builder()
        .ingredientName(ingredientName)
        .measureUnit(measureUnit.name())
        .quantity(quantity)
        .build();

    return RecipeCreatingDto.builder()
        .recipeName(recipeName)
        .steps(List.of(stepDescription))
        .ingredients(List.of(ingredientCreatingDto))
        .cookTime(cookTime)
        .build();
  }

  protected PagedResponse<RecipeDto> createRecipePagedResponse() {
    return createEmptyPagedResponse(List.of(createRecipeDto()));
  }

  protected PagedResponse<RecipeDto> createEmptyPagedResponse(List<RecipeDto> content) {
    return PagedResponse.<RecipeDto>builder()
        .size(size)
        .page(page)
        .content(content)
        .last(true)
        .totalElements(1)
        .totalPages(1)
        .build();
  }

  protected RecipeDto createRecipeDto() {
    return RecipeDto.builder()
        .id(id)
        .recipeName(recipeName)
        .recipePhotoLink(photoLink)
        .build();
  }

  protected Page<Recipe> createRecipePage() {
    List<Recipe> content = new ArrayList<>();
    content.add(createExistingRecipe());
    Page<Recipe> page = new PageImpl<>(content);
    return page;
  }

  protected MultipartFile createPhotoMultipartFile() throws IOException {
    Resource resource = new ClassPathResource(photoPath);
    File file = resource.getFile();

    MultipartFile multipartFile = new MockMultipartFile(photoName, photoName,
        MediaType.IMAGE_JPEG_VALUE, Files.readAllBytes(file.toPath()));
    return multipartFile;
  }
}
