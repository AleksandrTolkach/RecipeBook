package by.toukach.recipebook.exception;

/**
 * An exception class for entity not found case.
 */
public class EntityNotFoundException extends EntityException {

  public EntityNotFoundException(String message) {
    super(message);
  }
}
