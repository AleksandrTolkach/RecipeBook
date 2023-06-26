package by.toukach.recipebook.exception;

import by.toukach.recipebook.enumeration.MeasureUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A class that stores exception messages.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionMessage {
  public static final String USER_ID_NOT_FOUND = "User with id: %d is not found";
  public static final String USER_EXISTS = "User with this login already exists: %s";
  public static final String INCORRECT_LOGIN = "Incorrect login or password";
  public static final String TOKEN_EXPIRE = "Session time was expired. Please sign in again";
  public static final String TOKEN_NOT_FOUND = "Refresh token is not found. Please sign in";
  public static final String RECIPE_NOT_FOUND = "Recipe with id: %s is not found";
  public static final String OWNER_CONFLICT = "It is forbidden to modify other people's recipes";
  public static final String INVALID_MEASURE_UNIT
      = String.format("Invalid type of ingredient measure unit. Available units: %s",
      MeasureUnit.getNames());
  public static final String MINIO_PUT_OBJECT_ERROR = "Couldn't put object into S3 storage";
  public static final String STEP_PHOTO_MISMATCH_COUNT = "Steps quantity and steps' photo quantity"
      + " have different value";
  public static final String INVALID_CONTENT_TYPE = "Invalid content type: %s";
}
