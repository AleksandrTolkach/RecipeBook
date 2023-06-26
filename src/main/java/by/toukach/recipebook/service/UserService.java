package by.toukach.recipebook.service;

import by.toukach.recipebook.dto.SignInDto;
import by.toukach.recipebook.dto.UserCreatingDto;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.dto.UserUpdatingDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * An interface for managing users.
 */
public interface UserService {

  /**
   * This method is responsible for creating a new user.
   *
   * @param userCreatingDto an object storing data to create a new user
   * @return UserDto
   */
  UserDto createUser(UserCreatingDto userCreatingDto);

  /**
   * This method is responsible for finding a user by id.
   *
   * @param id The id of a specific user
   * @return UserDto
   */
  UserDto findUser(long id);

  /**
   * This method is responsible for finding a user by login.
   *
   * @param login The login of a specific user
   * @return UserDto
   */
  UserDto findUserByLogin(String login);

  /**
   * This method is responsible for finding a user profile.
   *
   * @return UserDto
   */
  UserDto profile();

  /**
   * This method is responsible for updating user's data.
   *
   * @param userUpdatingDto information to update
   * @return SignInDto tokens and authenticated user
   */
  SignInDto update(UserUpdatingDto userUpdatingDto);


  /**
   * This method is responsible for updating user's photo.
   *
   * @param photo photo to update
   * @return UserDto
   */
  UserDto updateUserPhoto(MultipartFile photo);
}
