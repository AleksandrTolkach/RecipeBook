package by.toukach.recipebook.service.impl;

import by.toukach.recipebook.converter.UserConverter;
import by.toukach.recipebook.dto.SignInDto;
import by.toukach.recipebook.dto.UserCreatingDto;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.dto.UserUpdatingDto;
import by.toukach.recipebook.entity.User;
import by.toukach.recipebook.entity.UserDetailsImpl;
import by.toukach.recipebook.enumeration.Role;
import by.toukach.recipebook.exception.EntityNotFoundException;
import by.toukach.recipebook.exception.ExceptionMessage;
import by.toukach.recipebook.repository.UserRepository;
import by.toukach.recipebook.service.RefreshTokenService;
import by.toukach.recipebook.service.S3Service;
import by.toukach.recipebook.service.UserService;
import by.toukach.recipebook.utils.SecurityUtil;
import by.toukach.recipebook.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * A service class for managing users.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserValidator userValidator;
  private final RefreshTokenService refreshTokenService;
  private final S3Service s3Service;

  @Transactional
  @Override
  public UserDto createUser(UserCreatingDto userCreatingDto) {
    User user = UserConverter.convertToEntity(userCreatingDto);
    userValidator.validateLoginUniqueness(user.getLogin());

    user.setRole(Role.USER);

    String encodedPassword = passwordEncoder.encode(userCreatingDto.getPassword());
    user.setPassword(encodedPassword);
    User save = userRepository.save(user);
    return UserConverter.convertToDto(save);
  }

  @Override
  public UserDto findUser(long id) {
    User user = findUserByIdIfExists(id);
    return convertToUserDto(user);
  }

  @Override
  public UserDto findUserByLogin(String login) {
    User user = findUserByLoginIfExists(login);
    return convertToUserDto(user);
  }

  @Override
  public UserDto profile() {
    Long id = SecurityUtil.getUserDetails().getId();
    User user = findUserByIdIfExists(id);
    return convertToUserDto(user);
  }

  @Transactional
  @Override
  public SignInDto update(UserUpdatingDto userUpdatingDto) {
    UserDetailsImpl userDetails = SecurityUtil.getUserDetails();
    Long id = userDetails.getId();

    User user = findUserByIdIfExists(id);
    user.setUserPhotoName(userDetails.getUserPhotoName());

    updateUserFields(user, userUpdatingDto);

    UserDto userDto = UserConverter.convertToDto(user);
    String userPhoto = user.getUserPhotoName();
    if (userPhoto != null && !userPhoto.isEmpty()) {
      String userPhotoLink = s3Service.getObjectUrl(userPhoto);
      userDto.setUserPhotoLink(userPhotoLink);
    }
    return refreshTokenService.updateTokens(userDto);
  }

  @Transactional
  @Override
  public UserDto updateUserPhoto(MultipartFile photo) {
    Long id = SecurityUtil.getUserDetails().getId();
    User user = findUserByIdIfExists(id);

    String userPhotoName = user.getUserPhotoName();

    if (userPhotoName != null && !userPhotoName.isEmpty()) {
      s3Service.deleteObject(userPhotoName);
    }

    String photoName = s3Service.putObject(photo);
    user.setUserPhotoName(photoName);
    return convertToUserDto(user);
  }

  private void updateUserFields(User user, UserUpdatingDto userUpdatingDto) {
    String newLogin = userUpdatingDto.getLogin();
    if (!user.getLogin().equals(newLogin)) {
      userValidator.validateLoginUniqueness(newLogin);
    }

    user.setLogin(newLogin);
    user.setFirstName(userUpdatingDto.getFirstName());
    user.setLastName(userUpdatingDto.getLastName());
  }

  private User findUserByIdIfExists(Long id) {

    return userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(String
            .format(ExceptionMessage.USER_ID_NOT_FOUND, id)));
  }

  private User findUserByLoginIfExists(String login) {
    return userRepository.findByLogin(login).orElseThrow(
        () -> new EntityNotFoundException(ExceptionMessage.INCORRECT_LOGIN));
  }

  private UserDto convertToUserDto(User user) {
    String userPhotoName = user.getUserPhotoName();
    String userPhotoLink = userPhotoName != null ? s3Service.getObjectUrl(userPhotoName) : null;

    UserDto userDto = UserConverter.convertToDto(user);
    userDto.setUserPhotoLink(userPhotoLink);

    return userDto;
  }
}
