package cluster.security.securityservice.service.impl.token;

public interface RefreshTokenService extends JwtGeneration {

    boolean isTokenExpired(String token);
}
