package cluster.security.securityservice.service.impl.token;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtGeneration {

    String generateToken(UserDetails userDetails);

}
