package by.toukach.recipebook.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * A class that represents an error body.
 */
@Getter
@Setter
public class BaseError {
  private String message;
}
