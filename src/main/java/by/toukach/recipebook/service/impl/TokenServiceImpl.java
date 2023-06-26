package by.toukach.recipebook.service.impl;

import by.toukach.recipebook.entity.UserDetailsImpl;
import by.toukach.recipebook.service.TokenService;
import by.toukach.recipebook.utils.SecurityUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

/**
 * Implementation of TokenProcessor. Responsible for handling intermediate operations on tokens.
 */
@Slf4j
@Component
public class TokenServiceImpl implements TokenService {
  @Value("${app.security.secret}")
  private String secret;
  @Value("${app.security.access-cookie-name}")
  private String accessCookieName;
  @Value("${app.security.refresh-cookie-name}")
  private String refreshCookieName;
  @Value("${app.security.access-token-expiration-time-min}")
  private long tokenExpirationTimeMin;
  @Value("${app.security.path}")
  private String path;
  @Value("${app.security.access-cookie-duration-sec}")
  private long accessCookieDurationSec;
  @Value("${app.security.refresh-cookie-duration-sec}")
  private long refreshCookieDurationSec;

  @Override
  public ResponseCookie generateAccessJwtCookie(String username) {
    String jwt = generateTokenFromUsername(username);
    return generateCookie(accessCookieName, jwt);
  }

  @Override
  public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
    return generateCookie(refreshCookieName, refreshToken);
  }

  @Override
  public String getJwtAccessFromCookies(HttpServletRequest request) {
    return getCookieValueByName(request, accessCookieName);
  }

  @Override
  public String getJwtRefreshFromCookies(HttpServletRequest request) {
    return getCookieValueByName(request, refreshCookieName);
  }

  @Override
  public ResponseCookie getCleanJwtAccessCookie() {
    return ResponseCookie.from(accessCookieName, null).path(path).build();
  }

  @Override
  public ResponseCookie getCleanJwtRefreshCookie() {
    return ResponseCookie.from(refreshCookieName, null).path(path).build();
  }

  @Override
  public String getUsernameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
  }

  @Override
  public boolean validateJwtToken(String token) {
    try {
      Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
      return true;
    } catch (SignatureException | MalformedJwtException | ExpiredJwtException
        | UnsupportedJwtException | IllegalArgumentException e) {
      log.error("Invalid JWT signature: {}", e.getMessage());
    }
    return false;
  }

  @Override
  public String generateTokenFromUsername(String username) {
    long expirationTimeMillis = TimeUnit.MINUTES.toMillis(tokenExpirationTimeMin);
    Date expirationDate =
        new Date(new Date().getTime() + expirationTimeMillis);
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(expirationDate)
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  @Override
  public void updateUserCredentialsInContext(String username) {
    UserDetailsImpl userDetails = SecurityUtil.getUserDetails();
    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  /**
   * Generates cookie.
   *
   * @param name cookie name
   * @param value payload
   * @return generated cookie
   */
  private ResponseCookie generateCookie(String name, String value) {
    long maxAge =
        name.equals(accessCookieName) ? accessCookieDurationSec : refreshCookieDurationSec;
    return ResponseCookie.from(name, value)
        .path(path)
        .maxAge(maxAge)
        .httpOnly(true)
        .secure(false)
        .build();
  }

  /**
   * Gets cookie's value by cookie name.
   *
   * @param request http request
   * @param name cookie name
   * @return payload
   */
  private static String getCookieValueByName(HttpServletRequest request, String name) {
    Cookie cookie = WebUtils.getCookie(request, name);
    return cookie != null ? cookie.getValue() : null;
  }
}
