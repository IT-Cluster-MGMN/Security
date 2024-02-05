package cluster.security.securityservice.service.impl.token;

public interface AccessTokenService extends JwtGeneration {

    String generateAccessTokenFromRefresh(String refreshToken);

    boolean isTokenExpired(String token);

    String getUsernameFromToken(String token);
}
