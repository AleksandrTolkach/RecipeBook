package by.toukach.recipebook.service.impl;

import by.toukach.recipebook.entity.User;
import by.toukach.recipebook.entity.UserDetailsImpl;
import by.toukach.recipebook.exception.EntityNotFoundException;
import by.toukach.recipebook.exception.ExceptionMessage;
import by.toukach.recipebook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementation of user details management service.
 */
@Service
@Primary
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByLogin(username).orElseThrow(
        () -> new EntityNotFoundException(ExceptionMessage.INCORRECT_LOGIN));
    return new UserDetailsImpl(user);
  }
}
