package by.toukach.recipebook.service;

import by.toukach.recipebook.dto.SignInDto;
import by.toukach.recipebook.dto.SignInRequestDto;
import by.toukach.recipebook.dto.UserCreatingDto;

/**
 * User authorization interface.
 */
public interface AuthService {

  /**
   * Allows the user to sign in into application.
   *
   * @param signInRequestDto sign in details
   * @return tokens and authenticated user
   */
  SignInDto signIn(SignInRequestDto signInRequestDto);

  /**
   * Allows the user to sign up into application.
   *
   * @param userCreatingDto information of the user
   * @return tokens and authenticated user
   */
  SignInDto signUp(UserCreatingDto userCreatingDto);

  /**
   * Extends duration of the token.
   *
   * @param refreshToken refresh token
   * @return tokens and authenticated user
   */
  SignInDto refresh(String refreshToken);

  /**
   * Clears user's tokens in system.
   *
   * @return cleared tokens
   */
  SignInDto logout();
}
