package by.toukach.recipebook.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.toukach.recipebook.BaseTest;
import by.toukach.recipebook.RecipeBookApplication;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.entity.RefreshToken;
import by.toukach.recipebook.entity.User;
import by.toukach.recipebook.repository.RefreshTokenRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RecipeBookApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(locations = "classpath:application-test.yaml")
public class AuthControllerIntegrationTest extends BaseTest {
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private RefreshTokenRepository refreshTokenRepository;
  @Value("${app.security.secret}")
  private String secret;
  private Long userId;
  private String signInRequest;

  @BeforeEach
  void init() throws Exception {
    User user = createNewUser();
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userId = userRepository.save(user).getId();

    final File signInRequestFile = new ClassPathResource("json/sign-in-request.json").getFile();
    signInRequest = Files.readString(signInRequestFile.toPath());
  }

  @Test
  void signInTest_should_SignIn() throws Exception {
    MvcResult mvcResult = signIn(signInRequest);

    UserDto actualResult = readValue(mvcResult.getResponse().getContentAsString(),
        new TypeReference<UserDto>() {
        });
    UserDto expectedResult = findExpectedUser(userId);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void signInTest_shouldThrowError_WhenSignInWithWrongPassword() throws Exception {
    final File signInRequestWithWrongPasswordFile =
        new ClassPathResource("json/wrong-password-sign-in-request.json").getFile();
    String signInRequestWithWrongPassword =
        Files.readString(signInRequestWithWrongPasswordFile.toPath());

    mockMvc.perform(post(signInPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(signInRequestWithWrongPassword))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void signInTest_shouldThrowError_WhenSignInWithWrongLogin() throws Exception {
    final File signInRequestWithWrongPasswordFile =
        new ClassPathResource("json/wrong-login-sign-in-request.json").getFile();
    String signInRequestWithWrongLogin =
        Files.readString(signInRequestWithWrongPasswordFile.toPath());

    mockMvc.perform(post(signInPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(signInRequestWithWrongLogin))
        .andExpect(status().isNotFound());
  }

  @Test
  void signInTest_shouldThrowError_WhenSignInWithEmptyLoginAndPassword() throws Exception {
    final File signInRequestWithWrongPasswordFile =
        new ClassPathResource("json/empty-login-and-password-sign-in-request.json").getFile();
    String signInRequestWithWrongLogin =
        Files.readString(signInRequestWithWrongPasswordFile.toPath());

    mockMvc.perform(post(signInPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(signInRequestWithWrongLogin))
        .andExpect(status().isBadRequest());
  }

  @Test
  void signUpTest_should_SignUp() throws Exception {
    final File signUpRequest = new ClassPathResource("json/sign-up-request.json").getFile();
    String signUp = Files.readString(signUpRequest.toPath());

    MvcResult mvcResult = mockMvc.perform(post(signUpPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(signUp))
        .andReturn();

    UserDto actualResult = readValue(mvcResult.getResponse().getContentAsString(),
        new TypeReference<UserDto>() {
        });
    Long id = actualResult.getId();
    UserDto expectedResult = findExpectedUser(id);

    assertEquals(expectedResult, actualResult);
  }

  @Test
  void signUpTest_shouldThrowError_WhenSignUpWithWeakPassword() throws Exception {
    final File signUpRequest = new ClassPathResource("json/weak-password-sign-up-request.json")
        .getFile();
    String signUp = Files.readString(signUpRequest.toPath());

    mockMvc.perform(post(signUpPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(signUp))
        .andExpect(status().isBadRequest());
  }

  @Test
  void signUpTest_shouldThrowError_WhenSignUpWithExistingLogin() throws Exception {
    final File signUpRequest = new ClassPathResource("json/sign-in-request.json")
        .getFile();
    String signUp = Files.readString(signUpRequest.toPath());

    mockMvc.perform(post(signUpPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(signUp))
        .andExpect(status().isBadRequest());
  }

  @Test
  void signUpTest_shouldThrowError_WhenSignUpWithInvalidFields() throws Exception {
    final File signUpRequest = new ClassPathResource("json/invalid-fields-sign-up-request.json")
        .getFile();
    String signUp = Files.readString(signUpRequest.toPath());

    mockMvc.perform(post(signUpPath)
            .contentType(MediaType.APPLICATION_JSON)
            .content(signUp))
        .andExpect(status().isBadRequest());
  }

  @Test
  void refreshTest_should_RefreshToken() throws Exception {
    MvcResult mvcSignInResult = signIn(signInRequest);

    Cookie[] signInCookies = mvcSignInResult.getResponse().getCookies();
    Cookie signInAccessTokenCookie = getCookieByName(signInCookies, accessToken);
    Cookie signInRefreshTokenCookie = getCookieByName(signInCookies, refreshToken);
    String signInRefreshTokenValue = signInRefreshTokenCookie.getValue();
    Date signInExpirationAccessToken = getExpirationTimeFromCookie(signInAccessTokenCookie);
    RefreshToken signInRefreshToken = findTokenByValue(signInRefreshTokenValue);

    Thread.sleep(2000);

    MvcResult mvcRefreshResult = mockMvc.perform(post(refreshPath)
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(mvcSignInResult.getResponse().getCookies()))
        .andReturn();

    Cookie[] refreshCookies = mvcRefreshResult.getResponse().getCookies();
    Cookie newAccessTokenCookie = getCookieByName(refreshCookies, accessToken);
    Cookie newRefreshTokenCookie = getCookieByName(refreshCookies, refreshToken);
    Date newExpirationAccessToken = getExpirationTimeFromCookie(newAccessTokenCookie);
    String newRefreshTokenValue = newRefreshTokenCookie.getValue();
    RefreshToken newRefreshToken = findTokenByValue(newRefreshTokenValue);

    assertTrue(newExpirationAccessToken.after(signInExpirationAccessToken));
    assertTrue(newRefreshToken.getExpiryDate().isAfter(signInRefreshToken.getExpiryDate()));
  }

  @Test
  void refreshTest_should_ThrowError_WhenRefreshWithoutToken() throws Exception {
    mockMvc.perform(post(refreshPath)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  void refreshTest_should_ThrowError_WhenRefreshWithUnExistingToken() throws Exception {
    MvcResult mvcSignInResult = signIn(signInRequest);

    Cookie[] signInCookies = mvcSignInResult.getResponse().getCookies();
    Cookie signInRefreshTokenCookie = getCookieByName(signInCookies, refreshToken);
    String unExistingRefreshToken = UUID.randomUUID().toString();
    signInRefreshTokenCookie.setValue(unExistingRefreshToken);

    mockMvc.perform(post(refreshPath)
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(signInCookies))
        .andExpect(status().isBadRequest());
  }

  @Test
  void logoutTest_should_Logout() throws Exception {
    MvcResult mvcResult = signIn(signInRequest);

    mockMvc.perform(post(logoutPath)
            .contentType(MediaType.APPLICATION_JSON)
            .cookie(mvcResult.getResponse().getCookies()))
        .andExpect(cookie().value(accessToken, ""))
        .andExpect(cookie().value(refreshToken, ""));
  }

  @Test
  void logoutTest_should_ThrowError_WhenLogoutUnauthorizedUser() throws Exception {
    mockMvc.perform(post(logoutPath)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  private Cookie getCookieByName(Cookie[] cookies, String cookieName) {
    return Arrays.stream(cookies)
        .filter(cookie -> cookie.getName().equals(cookieName)).findFirst()
        .orElse(null);
  }

  private Date getExpirationTimeFromCookie(Cookie cookie) {
    return Jwts.parser().setSigningKey(secret)
        .parseClaimsJws(cookie.getValue()).getBody().getExpiration();
  }

  private RefreshToken findTokenByValue(String value) {
    return refreshTokenRepository.findByToken(value)
        .orElse(null);
  }
}
