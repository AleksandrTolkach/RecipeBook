package by.toukach.recipebook.validation.impl;

import by.toukach.recipebook.exception.EntityExistsException;
import by.toukach.recipebook.exception.ExceptionMessage;
import by.toukach.recipebook.repository.UserRepository;
import by.toukach.recipebook.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * A class for validating input related to users.
 */
@Component
@RequiredArgsConstructor
public class UserValidatorImpl implements UserValidator {
  private final UserRepository userRepository;

  @Override
  public void validateLoginUniqueness(String login) {
    if (userRepository.existsByLogin(login)) {
      throw new EntityExistsException(String
          .format(ExceptionMessage.USER_EXISTS, login));
    }
  }
}
