package by.toukach.recipebook.controller.listener;

import by.toukach.recipebook.entity.Ingredient;
import by.toukach.recipebook.entity.Recipe;
import by.toukach.recipebook.entity.Step;
import by.toukach.recipebook.entity.User;
import by.toukach.recipebook.enumeration.MeasureUnit;
import by.toukach.recipebook.enumeration.Role;
import by.toukach.recipebook.repository.RecipeRepository;
import by.toukach.recipebook.repository.UserRepository;
import by.toukach.recipebook.service.S3Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * A listener that responsible for adding default date during starting application if needed.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStateListener {
  private static final String FIRST_NAME = "Opa";
  private static final String LAST_NAME = "Apo";
  private static final String LOGIN = "opa";
  private static final String RECIPE_NAME = "Cucumber salad with garlic";
  private static final String STEP_1 = "Для начала подготовьте необходимые ингредиенты по списку.";
  private static final String STEP_2 = "Огурцы вымойте и нарежьте тонкими кружочками.";
  private static final String STEP_3 = "Укроп вымойте, обсушите и мелко порубите. "
      + "По желанию можете заменить или дополнить другой зеленью, например, петрушкой, "
      + "зеленым луком, мятой, шпинатом, черемшой или использовать смесь разных трав.";
  private static final String STEP_4 = "В миске соедините огурцы, укроп и пропущенные через пресс "
      + "зубчики чеснока.";
  private static final String STEP_5 = "Салат полейте растительным маслом, посолите и перемешайте. "
      + "Дайте салату постоять 5 минут и как следует пропитаться.";
  private static final String INGREDIENT_1 = "Огурцы";
  private static final String INGREDIENT_2 = "Укроп";
  private static final String INGREDIENT_3 = "Растительное масло";
  private static final String INGREDIENT_4 = "Чеснок";
  private static final String INGREDIENT_5 = "Соль";
  private static final String PASSWORD = "1234_Aapo";
  private static final String USER_PHOTO_PATH = "static/images/userPhoto.jpg";
  private static final String RECIPE_PHOTO_PATH = "static/images/recipePhoto.jpg";
  private static final String STEP_PHOTO_PATH = "static/images/step%s.jpg";

  private final S3Service s3Service;
  private final UserRepository userRepository;
  private final RecipeRepository recipeRepository;
  private final PasswordEncoder passwordEncoder;


  /**
   * This method is responsible for adding default data into database during starting application.
   */
  @EventListener(ContextRefreshedEvent.class)
  public void handle() {
    if (recipeRepository.count() == 0) {
      Resource userPhotoResource = new ClassPathResource(USER_PHOTO_PATH);
      Resource recipePhotoResource = new ClassPathResource(RECIPE_PHOTO_PATH);

      File userPhotoFile = null;
      File recipePhotoFile = null;

      MultipartFile userPhoto = null;
      LocalMultipartFileImpl recipePhoto = null;
      MultipartFile[] stepPhoto = new MultipartFile[5];

      String userPhotoName = null;
      String recipePhotoName = null;
      String[] stepPhotoNames = new String[5];

      try {
        userPhotoFile = userPhotoResource.getFile();
        userPhoto = new LocalMultipartFileImpl(UUID.randomUUID().toString(),
            userPhotoFile.getName(),
            MediaType.IMAGE_JPEG_VALUE, Files.readAllBytes(userPhotoFile.toPath()));
        userPhotoName = s3Service.putObject(userPhoto);

        recipePhotoFile = recipePhotoResource.getFile();
        recipePhoto = new LocalMultipartFileImpl(UUID.randomUUID().toString(),
            recipePhotoFile.getName(),
            MediaType.IMAGE_JPEG_VALUE, Files.readAllBytes(recipePhotoFile.toPath()));
        recipePhotoName = s3Service.putObject(recipePhoto);

        Resource[] stepResources = new Resource[5];
        File[] stepPhotoFiles = new File[5];
        for (int i = 0; i < 5; i++) {
          stepResources[i] = new ClassPathResource(String.format(STEP_PHOTO_PATH, i + 1));
          stepPhotoFiles[i] = stepResources[i].getFile();
          stepPhoto[i] = new LocalMultipartFileImpl(UUID.randomUUID().toString(),
              stepPhotoFiles[i].getName(), MediaType.IMAGE_JPEG_VALUE,
              Files.readAllBytes(stepPhotoFiles[i].toPath()));
          stepPhotoNames[i] = s3Service.putObject(stepPhoto[i]);
        }

      } catch (IOException e) {
        log.info("Couldn't read files");
      }

      String password = passwordEncoder.encode(PASSWORD);

      User user = User.builder()
          .firstName(FIRST_NAME)
          .lastName(LAST_NAME)
          .login(LOGIN)
          .password(password)
          .role(Role.USER)
          .userPhotoName(userPhotoName)
          .build();
      userRepository.save(user);

      List<Step> steps = new ArrayList<>();

      steps.add(new Step(STEP_1, stepPhotoNames[0]));
      steps.add(new Step(STEP_2, stepPhotoNames[1]));
      steps.add(new Step(STEP_3, stepPhotoNames[2]));
      steps.add(new Step(STEP_4, stepPhotoNames[3]));
      steps.add(new Step(STEP_5, stepPhotoNames[4]));

      List<Ingredient> ingredients = new ArrayList<>();

      ingredients.add(new Ingredient(INGREDIENT_1, 400, MeasureUnit.GRAM));
      ingredients.add(new Ingredient(INGREDIENT_2, 60, MeasureUnit.GRAM));
      ingredients.add(new Ingredient(INGREDIENT_3, 60, MeasureUnit.MILLILITER));
      ingredients.add(new Ingredient(INGREDIENT_4, 3, MeasureUnit.PCS));
      ingredients.add(new Ingredient(INGREDIENT_5, 1, MeasureUnit.GRAM));

      Recipe recipe = Recipe.builder()
          .recipeName(RECIPE_NAME)
          .author(user)
          .steps(steps)
          .cookTime(20)
          .ingredients(ingredients)
          .saves(List.of(user))
          .recipePhotoName(recipePhotoName)
          .build();

      recipeRepository.save(recipe);

      log.info("Default data successfully added into database");
    } else {
      log.info("Default data already exist.");
    }
  }
}
