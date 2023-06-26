package by.toukach.recipebook.utils;

import by.toukach.recipebook.entity.UserDetailsImpl;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for working with SecurityContext.
 */
@UtilityClass
public class SecurityUtil {

  /**
   * Gets user details from security context.
   *
   * @return user's detail data
   */
  public static UserDetailsImpl getUserDetails() {
    return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
