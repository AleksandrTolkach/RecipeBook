package by.toukach.recipebook.controller;

import by.toukach.recipebook.dto.SignInDto;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.dto.UserUpdatingDto;
import by.toukach.recipebook.repository.UserRepository;
import by.toukach.recipebook.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * A controller class for User entity.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserRepository userRepository;

  /**
   * This method is responsible for finding a specific user by id.
   *
   * @param id the id of a specific user
   * @return UserDto
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('USER')")
  public UserDto findUser(@PathVariable Long id) {
    log.info(String.format("GET-request: getting user with id %s", id));
    return userService.findUser(id);
  }

  /**
   * This method is responsible for finding an authenticated user's profile.
   *
   * @return the profile of the current user
   */
  @GetMapping("/profile")
  @PreAuthorize("hasRole('USER')")
  public UserDto profile() {
    log.info("GET-request: getting user profile");
    return userService.profile();
  }

  /**
   * This method is responsible for updating an authenticated user.
   */
  @PutMapping
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<UserDto> update(@RequestBody @Valid UserUpdatingDto userUpdatingDto) {
    log.info("PUT-request: update user");
    return createResponse(userService.update(userUpdatingDto));
  }

  /**
   * This method is responsible for updating an authenticated user's photo.
   */
  @PutMapping("/photo")
  @PreAuthorize("hasRole('USER')")
  public UserDto updateUserPhoto(@RequestParam MultipartFile photo) {
    log.info("PUT-request: update user's photo");
    return userService.updateUserPhoto(photo);
  }

  private ResponseEntity<UserDto> createResponse(SignInDto signInDto) {
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, signInDto.getAccessTokenCookie())
        .header(HttpHeaders.SET_COOKIE, signInDto.getRefreshTokenCookie())
        .body(signInDto.getUser());
  }
}
