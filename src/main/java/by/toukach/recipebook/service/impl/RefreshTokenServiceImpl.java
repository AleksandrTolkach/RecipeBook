package by.toukach.recipebook.service.impl;

import by.toukach.recipebook.converter.RefreshTokenConverter;
import by.toukach.recipebook.converter.UserConverter;
import by.toukach.recipebook.dto.RefreshTokenDto;
import by.toukach.recipebook.dto.SignInDto;
import by.toukach.recipebook.dto.UserDto;
import by.toukach.recipebook.entity.RefreshToken;
import by.toukach.recipebook.entity.User;
import by.toukach.recipebook.exception.ExceptionMessage;
import by.toukach.recipebook.exception.TokenRefreshException;
import by.toukach.recipebook.repository.RefreshTokenRepository;
import by.toukach.recipebook.service.RefreshTokenService;
import by.toukach.recipebook.service.TokenService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of token management service.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
  @Value("${app.security.refresh-token-expiration-time-day}")
  private long refreshTokenExpirationTimeDay;
  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenService tokenService;

  @Override
  @Transactional
  public RefreshTokenDto createRefreshToken(UserDto userDto) {
    Long userId = userDto.getId();
    Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByUserId(userId);
    RefreshToken refreshToken = refreshTokenOptional.orElseGet(() -> RefreshToken.builder()
        .user(UserConverter.convertToEntity(userDto))
        .build());

    Instant expiryDate = Instant.now().plus(refreshTokenExpirationTimeDay, ChronoUnit.DAYS);
    refreshToken.setExpiryDate(expiryDate);
    refreshToken.setToken(UUID.randomUUID().toString());

    RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);
    return RefreshTokenConverter.convertToDto(savedRefreshToken);
  }

  @Override
  @Transactional
  public SignInDto refreshAccessToken(String refreshToken) {
    RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
        .orElseThrow(() -> new TokenRefreshException(ExceptionMessage.TOKEN_NOT_FOUND));
    Instant expiryDate = token.getExpiryDate();

    if (expiryDate.isBefore(Instant.now())) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(ExceptionMessage.TOKEN_EXPIRE);
    }

    User user = token.getUser();

    ResponseCookie accessTokenCookie = tokenService.generateAccessJwtCookie(user.getLogin());
    ResponseCookie refreshTokenCookie = tokenService.generateRefreshJwtCookie(
        createRefreshToken(UserConverter.convertToDto(user)).getToken());
    return SignInDto.builder()
        .accessTokenCookie(accessTokenCookie.toString())
        .refreshTokenCookie(refreshTokenCookie.toString())
        .user(UserConverter.convertToDto(user))
        .build();
  }

  @Override
  @Transactional
  public SignInDto updateTokens(UserDto userDto) {
    tokenService.updateUserCredentialsInContext(userDto.getLogin());
    deleteByUserId(userDto.getId());

    RefreshTokenDto refreshTokenDto = createRefreshToken(userDto);
    ResponseCookie accessTokenCookie = tokenService.generateAccessJwtCookie(userDto.getLogin());
    ResponseCookie refreshTokenCookie = tokenService.generateRefreshJwtCookie(
        refreshTokenDto.getToken());
    return SignInDto.builder()
        .accessTokenCookie(accessTokenCookie.toString())
        .refreshTokenCookie(refreshTokenCookie.toString())
        .user(userDto)
        .build();
  }

  @Override
  @Transactional
  public void deleteByUserId(Long userId) {
    refreshTokenRepository.deleteRefreshTokenByUserId(userId);
  }
}
