package cluster.security.securityservice.service.token;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtGeneration {

    String generateToken(UserDetails userDetails);

}
