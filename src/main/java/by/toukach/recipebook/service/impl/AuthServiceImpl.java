package by.toukach.recipebook.service.impl;

import by.toukach.recipebook.dto.RefreshTokenDto;
import by.toukach.recipebook.dto.SignInDto;
import by.toukach.recipebook.dto.SignInRequestDto;
import by.toukach.recipebook.dto.UserCreatingDto;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.entity.UserDetailsImpl;
import by.toukach.recipebook.exception.ExceptionMessage;
import by.toukach.recipebook.exception.TokenRefreshException;
import by.toukach.recipebook.service.AuthService;
import by.toukach.recipebook.service.RefreshTokenService;
import by.toukach.recipebook.service.TokenService;
import by.toukach.recipebook.service.UserService;
import by.toukach.recipebook.utils.SecurityUtil;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AuthService interface.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenService refreshTokenService;
  private final UserService userService;
  private final TokenService tokenService;

  @Override
  public SignInDto signIn(SignInRequestDto signInRequestDto) {
    String login = signInRequestDto.getLogin();
    UserDto userDto = userService.findUserByLogin(login);
    Authentication authentication = setAuthentication(login,
        signInRequestDto.getPassword());
    return authenticateUser((UserDetailsImpl) authentication.getPrincipal(), userDto);
  }

  @Override
  @Transactional
  public SignInDto signUp(UserCreatingDto userCreatingDto) {
    UserDto userDto = userService.createUser(userCreatingDto);
    Authentication authentication = setAuthentication(userCreatingDto.getLogin(),
        userCreatingDto.getPassword());
    return authenticateUser((UserDetailsImpl) authentication.getPrincipal(), userDto);
  }

  @Override
  public SignInDto refresh(String refreshToken) {
    if (StringUtils.isNotBlank(refreshToken)) {
      return refreshTokenService.refreshAccessToken(refreshToken);
    } else {
      throw new TokenRefreshException(ExceptionMessage.TOKEN_EXPIRE);
    }
  }

  @Override
  public SignInDto logout() {
    Long id = SecurityUtil.getUserDetails().getId();
    refreshTokenService.deleteByUserId(id);
    ResponseCookie accessTokenCookie = tokenService.getCleanJwtAccessCookie();
    ResponseCookie refreshTokenCookie = tokenService.getCleanJwtRefreshCookie();
    return SignInDto.builder()
        .accessTokenCookie(accessTokenCookie.toString())
        .refreshTokenCookie(refreshTokenCookie.toString())
        .build();
  }

  private SignInDto authenticateUser(UserDetailsImpl userDetails, UserDto userDto) {
    ResponseCookie accessTokenCookie =
        tokenService.generateAccessJwtCookie(userDetails.getUsername());
    RefreshTokenDto refreshTokenDto =
        refreshTokenService.createRefreshToken(userDto);
    ResponseCookie refreshTokenCookie = tokenService.generateRefreshJwtCookie(
        refreshTokenDto.getToken());
    return SignInDto.builder()
        .accessTokenCookie(accessTokenCookie.toString())
        .refreshTokenCookie(refreshTokenCookie.toString())
        .user(userDto)
        .build();
  }

  private Authentication setAuthentication(String login, String password) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(login, password));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return authentication;
  }
}
