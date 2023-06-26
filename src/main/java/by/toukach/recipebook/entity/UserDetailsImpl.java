package by.toukach.recipebook.entity;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implementation of UserDetails interface which contains id, username, password and roles.
 */
@Getter
public class UserDetailsImpl implements UserDetails {
  private final Long id;
  private final String username;
  private final String password;
  private final String userPhotoName;
  private final GrantedAuthority grantedAuthority;

  /**
   * A constructor for creating UserDetails from User entity.
   *
   * @param user entity from which the UserDetails is created
   */
  public UserDetailsImpl(User user) {
    id = user.getId();
    username = user.getLogin();
    password = user.getPassword();
    userPhotoName = user.getUserPhotoName();
    grantedAuthority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(grantedAuthority);
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
