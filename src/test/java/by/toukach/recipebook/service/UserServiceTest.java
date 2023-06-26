package by.toukach.recipebook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import by.toukach.recipebook.BaseTest;
import by.toukach.recipebook.dto.SignInDto;
import by.toukach.recipebook.dto.UserCreatingDto;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.dto.UserUpdatingDto;
import by.toukach.recipebook.entity.User;
import by.toukach.recipebook.exception.EntityExistsException;
import by.toukach.recipebook.exception.EntityNotFoundException;
import by.toukach.recipebook.repository.UserRepository;
import by.toukach.recipebook.service.impl.UserServiceImpl;
import by.toukach.recipebook.validation.UserValidator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest extends BaseTest {
  @InjectMocks
  private UserServiceImpl userService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private SecurityContext securityContext;
  @Mock
  private Authentication authentication;
  @Mock
  private UserValidator userValidator;
  @Mock
  private RefreshTokenService refreshTokenService;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private S3Service s3Service;
  private User user;
  private UserDto userDto;
  private UserCreatingDto userCreatingDto;
  private SignInDto signInDto;

  @BeforeEach
  public void init() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(createUserDetails());

    user = createNewUser();
    userDto = createUserDto();
    userCreatingDto = createUserCreatingDto();
    signInDto = createSignInDto();
  }

  @Test
  void profileTest_should_FindProfile() {
    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    UserDto actualResult = userService.profile();
    UserDto expectedResult = userDto;

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void profileTest_should_ThrowError_WhenGettingProfileUnExistingUser() {
    when(userRepository.findById(id)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class, () -> userService.profile());
  }

  @Test
  void updateTest_should_UpdateUser() {
    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    doNothing().when(userValidator).validateLoginUniqueness(login);
    when(refreshTokenService.updateTokens(userDto)).thenReturn(signInDto);

    SignInDto actualResult = userService.update(createUserUpdatingDto());
    SignInDto expectedResult = signInDto;

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void updateTest_should_ThrowError_WhenUpdatingUserWithUnExistingId() {
    when(userRepository.findById(id)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class, () -> userService.update(createUserUpdatingDto()));
  }

  @Test
  void updateTest_should_ThrowError_WhenUpdatingUserWithExistingLogin() {
    UserUpdatingDto userUpdatingDto = createUserUpdatingDto();
    userUpdatingDto.setLogin(existingLogin);
    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    doThrow(EntityExistsException.class).when(userValidator).validateLoginUniqueness(existingLogin);

    assertThrows(EntityExistsException.class, () -> userService.update(userUpdatingDto));
  }

  @Test
  void createUserTest_shouldCreateUser() {
    doNothing().when(userValidator).validateLoginUniqueness(login);
    when(passwordEncoder.encode(password)).thenReturn(password);
    when(userRepository.save(user)).thenReturn(user);
    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    UserDto actualUser = userService.createUser(userCreatingDto);
    UserDto expectedUser = userDto;

    assertEquals(expectedUser, actualUser);
  }

  @Test
  void createUserTest_shouldThrowEntityExistsException() {
    doThrow(EntityExistsException.class).when(userValidator).validateLoginUniqueness(login);

    assertThrows(EntityExistsException.class,
        () -> userService.createUser(userCreatingDto));
  }

  @Test
  void findUserTest_shouldFindUser() {
    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    UserDto actualUser = userService.findUser(id);
    UserDto expectedUser = userDto;

    assertEquals(expectedUser, actualUser);
  }

  @Test
  void findUserTest_shouldThrowError_WhenSearchingUnExistingUser() {
    when(userRepository.findById(id)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class, () -> userService.findUser(id));
  }

  @Test
  void findUserByLoginTest_should_FindUser() {
    when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

    UserDto actualResult = userService.findUserByLogin(login);
    UserDto expectedResult = userDto;

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void findUserByLoginTest_should_ThrowError_WhenSearchingUnExistingUser() {
    when(userRepository.findByLogin(login)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class, () -> userService.findUserByLogin(login));
  }
}
