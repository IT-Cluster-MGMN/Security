package cluster.security.securityservice.service;

import cluster.security.securityservice.model.dtos.UserRegistration;
import cluster.security.securityservice.model.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    void save(UserRegistration userRegistration);

    Optional<User> findByUsername(String username);

    List<User> findAll();
}
