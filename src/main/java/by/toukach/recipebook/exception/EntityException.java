package by.toukach.recipebook.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * A base class for entity-related exceptions.
 */
@Getter
@Setter
@AllArgsConstructor
public abstract class EntityException extends RuntimeException {
  private String message;
}
