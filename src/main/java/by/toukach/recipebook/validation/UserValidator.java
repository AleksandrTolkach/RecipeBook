package by.toukach.recipebook.validation;

/**
 * An interface for validating input related to users.
 */
public interface UserValidator {

  /**
   * Validates if login already exists and throws a EntityExistsException if it is.
   *
   * @param login login to validate
   */
  void validateLoginUniqueness(String login);
}
