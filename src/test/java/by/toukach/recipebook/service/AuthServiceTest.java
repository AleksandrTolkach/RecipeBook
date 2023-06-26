package by.toukach.recipebook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import by.toukach.recipebook.BaseTest;
import by.toukach.recipebook.dto.SignInDto;
import by.toukach.recipebook.dto.SignInRequestDto;
import by.toukach.recipebook.dto.UserCreatingDto;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.exception.EntityExistsException;
import by.toukach.recipebook.exception.EntityNotFoundException;
import by.toukach.recipebook.exception.TokenRefreshException;
import by.toukach.recipebook.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthServiceTest extends BaseTest {
  @InjectMocks
  private AuthServiceImpl authService;
  @Mock
  private AuthenticationManager authenticationManager;
  @Mock
  private UserService userService;
  @Mock
  private RefreshTokenService refreshTokenService;
  @Mock
  private TokenService tokenService;
  @Mock
  private Authentication authentication;
  @Mock
  private SecurityContext securityContext;
  private UserCreatingDto userCreatingDto;
  private UserDto userDto;
  private SignInDto signInDto;

  @BeforeEach
  void init() {
    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(createUserDetails());

    userCreatingDto = createUserCreatingDto();
    userDto = createUserDto();
    signInDto = createSignInDto();
  }

  @Test
  void signInTest_should_SuccessfullySignIn() {
    when(userService.findUserByLogin(login))
        .thenReturn(userDto);
    when(tokenService.generateAccessJwtCookie(login))
        .thenReturn(createAccessTokenCookie());
    when(refreshTokenService.createRefreshToken(userDto))
        .thenReturn(createRefreshTokenDto());
    when(tokenService.generateRefreshJwtCookie(refreshToken))
        .thenReturn(createRefreshTokenCookie());

    SignInDto actualResult = authService.signIn(createSignInRequestDto());
    SignInDto expectedResult = signInDto;

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void signInTest_should_ThrowError_WhenSignInWithNoneExistingLogin() {
    SignInRequestDto signInRequestDto = createSignInRequestDto();
    when(userService.findUserByLogin(login)).thenThrow(EntityNotFoundException.class);

    assertThrows(EntityNotFoundException.class,
        () -> authService.signIn(signInRequestDto));
  }

  @Test
  void signUpTest_should_SuccessfullySignUp() {
    when(userService.createUser(userCreatingDto))
        .thenReturn(userDto);
    when(tokenService.generateAccessJwtCookie(login))
        .thenReturn(createAccessTokenCookie());
    when(refreshTokenService.createRefreshToken(userDto))
        .thenReturn(createRefreshTokenDto());
    when(tokenService.generateRefreshJwtCookie(refreshToken))
        .thenReturn(createRefreshTokenCookie());

    SignInDto actualResult = authService.signUp(userCreatingDto);
    SignInDto expectedResult = signInDto;

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void signUpTest_should_ThrowError_WhenSignUpWithExistingLogin() {
    when(userService.createUser(userCreatingDto))
        .thenThrow(EntityExistsException.class);

    assertThrows(EntityExistsException.class,
        () -> authService.signUp(userCreatingDto));
  }

  @Test
  void refreshTest_should_SuccessfullyRefreshToken() {
    when(refreshTokenService.refreshAccessToken(refreshToken))
        .thenReturn(signInDto);

    SignInDto actualResult = authService.refresh(refreshToken);
    SignInDto expectedResult = signInDto;

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void refreshTest_should_ThrowError_WhenRefreshEmptyToken() {
    assertThrows(TokenRefreshException.class, () -> authService.refresh(""));
  }

  @Test
  void refreshTest_should_ThrowError_WhenRefreshExpiredToken() {
    when(refreshTokenService.refreshAccessToken(refreshToken))
        .thenThrow(TokenRefreshException.class);

    assertThrows(TokenRefreshException.class,
        () -> authService.refresh(refreshToken));
  }

  @Test
  void logoutTest_should_SuccessfullyLogout() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(createUserDetails());
    when(tokenService.getCleanJwtAccessCookie()).thenReturn(createAccessTokenCookie());
    when(tokenService.getCleanJwtRefreshCookie()).thenReturn(createRefreshTokenCookie());

    SignInDto actualResult = authService.logout();
    SignInDto expectedResult = createSignInDtoAfterLogout();

    assertEquals(expectedResult, actualResult);
  }
}
