package by.toukach.recipebook.utils;

import lombok.experimental.UtilityClass;

/**
 * Class for storing REGEX-es and their messages.
 */
@UtilityClass
public class Constraints {
  public static final String PASSWORD_REGEX =
      "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9@#$%^&+=]).{7,}$";
  public static final String PASSWORD_MESSAGE =
      "Password must be at least 7 characters long,  contain lower, upper,"
          + " not alphabetic symbols.";
  public static final String RECIPE_NAME_REGEX =
      "[\sa-zA-Z-]*";
  public static final String RECIPE_NAME_MESSAGE =
      "Recipe name must consists of latin letters, hyphens and spaces.";
}
