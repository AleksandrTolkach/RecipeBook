package by.toukach.recipebook.controller;

import by.toukach.recipebook.dto.SignInDto;
import by.toukach.recipebook.dto.SignInRequestDto;
import by.toukach.recipebook.dto.UserCreatingDto;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.service.AuthService;
import by.toukach.recipebook.service.TokenService;
import by.toukach.recipebook.utils.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller for handling authentication and authorization-related HTTP requests.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final TokenService tokenService;

  /**
   * Handles the user's authentication request.
   *
   * @param signInRequestDto sign in details
   * @return tokens and authenticated user
   */
  @PostMapping("/sign-in")
  @PreAuthorize("permitAll()")
  public ResponseEntity<UserDto> signIn(@RequestBody @Valid SignInRequestDto signInRequestDto) {
    log.info("POST-request: {} sign in", signInRequestDto.getLogin());
    return createResponse(authService.signIn(signInRequestDto));
  }

  /**
   * Handles the user's registration request.
   *
   * @param userCreatingDto new user's details
   * @return tokens and authenticated user
   */
  @PostMapping("/sign-up")
  @PreAuthorize("permitAll()")
  public ResponseEntity<UserDto> signUp(@RequestBody @Valid UserCreatingDto userCreatingDto) {
    log.info("POST-request: {} sign up", userCreatingDto.getLogin());
    return createResponse(authService.signUp(userCreatingDto));
  }

  /**
   * Handles the user's refresh token request.
   *
   * @param request http request data
   * @return refreshed tokens
   */
  @PostMapping("/refresh")
  @PreAuthorize("permitAll()")
  public ResponseEntity<UserDto> refresh(HttpServletRequest request) {
    log.info("POST-request: refresh token");
    String refreshToken = tokenService.getJwtRefreshFromCookies(request);
    return createResponse(authService.refresh(refreshToken));
  }

  /**
   * Handles the user's log out request.
   *
   * @return cleared tokens
   */
  @PostMapping("/logout")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<UserDto> logout() {
    log.info("POST-request: {} logout", SecurityUtil.getUserDetails());
    return createResponse(authService.logout());
  }

  private ResponseEntity<UserDto> createResponse(SignInDto signInDto) {
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, signInDto.getAccessTokenCookie())
        .header(HttpHeaders.SET_COOKIE, signInDto.getRefreshTokenCookie())
        .body(signInDto.getUser());
  }
}
