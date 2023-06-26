package by.toukach.recipebook.converter;

import by.toukach.recipebook.dto.UserCreatingDto;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A class for converting user entity to dto and vice versa.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConverter {

  /**
   * This method is responsible for converting User entity to UserDto.
   *
   * @param entity entity to be converted
   * @return UserDto
   */
  public static UserDto convertToDto(User entity) {
    return UserDto.builder()
        .id(entity.getId())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .login(entity.getLogin())
        .firstName(entity.getFirstName())
        .lastName(entity.getLastName())
        .build();
  }

  /**
   * This method is responsible for converting UserCreatingDto to User entity.
   *
   * @param dto UserCreatingDto object to be converted
   * @return User
   */
  public static User convertToEntity(UserCreatingDto dto) {
    return User.builder()
        .login(dto.getLogin())
        .firstName(dto.getFirstName())
        .lastName(dto.getLastName())
        .build();
  }

  /**
   * This method is responsible for converting UserCreatingDto to User entity.
   *
   * @param dto UserDto object to be converted
   * @return User
   */
  public static User convertToEntity(UserDto dto) {
    return User.builder()
        .id(dto.getId())
        .createdAt(dto.getCreatedAt())
        .updatedAt(dto.getUpdatedAt())
        .login(dto.getLogin())
        .firstName(dto.getFirstName())
        .lastName(dto.getLastName())
        .build();
  }
}
