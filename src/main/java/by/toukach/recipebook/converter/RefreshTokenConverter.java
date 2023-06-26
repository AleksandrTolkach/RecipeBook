package by.toukach.recipebook.converter;

import by.toukach.recipebook.dto.RefreshTokenDto;
import by.toukach.recipebook.entity.RefreshToken;
import by.toukach.recipebook.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A class for converting RefreshToken entity to dto and vice versa.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenConverter {

  /**
   * This method is responsible for converting RefreshToken entity to RefreshTokenDto.
   *
   * @param entity entity to be converted
   * @return converted dto
   */
  public static RefreshTokenDto convertToDto(RefreshToken entity) {
    return RefreshTokenDto.builder()
        .id(entity.getId())
        .userDto(UserConverter.convertToDto(entity.getUser()))
        .token(entity.getToken())
        .expiryDate(entity.getExpiryDate())
        .build();
  }

  /**
   * This method is responsible for converting RefreshTokenDto to RefreshToken entity.
   *
   * @param dto RefreshTokenDto object to be converted
   * @return converted entity
   */
  public static RefreshToken convertToEntity(RefreshTokenDto dto) {
    User user = User.builder().id(dto.getUserDto().getId()).build();
    return RefreshToken.builder()
        .id(dto.getId())
        .user(user)
        .token(dto.getToken())
        .expiryDate(dto.getExpiryDate())
        .build();
  }
}
