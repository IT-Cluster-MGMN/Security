package cluster.security.securityservice.service;


import cluster.security.securityservice.dao.UserJpaRepo;
import cluster.security.securityservice.exception.UsernameException;
import cluster.security.securityservice.model.dtos.UserRegistration;
import cluster.security.securityservice.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserJpaRepo userJpaRepo;
    private final AuthorityService authorityService;
    private final UserInfoService userInfoService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Implements authentication by retrieving user data from the database and comparing it with the user's input.
     *
     * @param username The username provided during login to identify the user.
     * @return A UserDetails object representing the authenticated user.
     * @throws UsernameNotFoundException Thrown if the provided username is not found in the database.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User '%s' was not found", username)));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>(List.of(new SimpleGrantedAuthority(user.getAuthority().getAuthority()))));
    }

    /**
     * Method for saving User, Authority and UserInfo to database
     *
     * @param userRegistration contains user's data. It is used for creating User,
     *                         Authority and UserInfo objects.
     *                         Current object we get during registration on the site
     * */
    @Transactional
    public void save(UserRegistration userRegistration) {
        if (findByUsername(userRegistration.getUser().getUsername()).isEmpty()) {
            userRegistration.setPassword("{bcrypt}" + passwordEncoder.encode(userRegistration.getUser().getPassword()));

            userJpaRepo.save(userRegistration.getUser());
            authorityService.saveAuthority(userRegistration.getAuthority());
            userInfoService.saveUserInfo(userRegistration.getUserInfo());
        } else {
            throw new UsernameException(
                    String.format("User with username '%s' already exists", userRegistration.getUser().getUsername()));
        }
    }

    /**
     * @return the user from a database according to the username
     * */
    public Optional<User> findByUsername(String username) {
        return userJpaRepo.findById(username);
    }

    /**
     * @return all users from a database
     * */
    public List<User> findAll() {
        return userJpaRepo.findAll();
    }
}
