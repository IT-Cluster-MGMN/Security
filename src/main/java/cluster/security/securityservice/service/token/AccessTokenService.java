package cluster.security.securityservice.service.token;


import cluster.security.securityservice.config.keys.AccessRsaKeyConfig;
import cluster.security.securityservice.config.keys.RefreshRsaKeyConfig;
import cluster.security.securityservice.dao.AuthorityJpaRepo;
import cluster.security.securityservice.service.UserService;
import cluster.security.securityservice.util.TokenType;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenService extends JwtHelper implements JwtGeneration {

    private final UserService userService;
    private final AuthorityJpaRepo authorityJpaRepo;

    public AccessTokenService(AccessRsaKeyConfig accessRsaKeyConfig,
                              RefreshRsaKeyConfig refreshRsaKeyConfig,
                              UserService userService,
                              AuthorityJpaRepo authorityJpaRepo) {
        super(accessRsaKeyConfig, refreshRsaKeyConfig);
        this.userService = userService;
        this.authorityJpaRepo = authorityJpaRepo;
    }


    public String generateAccessFromRefresh(String refreshToken) {
        if (isTokenExpired(refreshToken, TokenType.REFRESH)) {
            return null;
        }

        final String username = getAllClaimsFromToken(refreshToken, TokenType.REFRESH).getSubject();

        if (authorityJpaRepo.findById(username).isEmpty()) {
            return null;
        }

        final String roleFromClaims = getAllClaimsFromToken(refreshToken, TokenType.REFRESH).get("authority", String.class);
        final String roleFromDB = authorityJpaRepo.findById(username).get().getAuthority();

        if (!roleFromClaims.equals(roleFromDB)) {
            return null;
        }

        final UserDetails userDetails = userService.loadUserByUsername(username);

        return generateToken(userDetails);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return super.generateToken(userDetails, TokenType.ACCESS);
    }

    public String getUsernameFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token, TokenType.ACCESS);
        final String username = claims.getSubject();

        return (!isTokenExpired(token, TokenType.ACCESS) && userService.findByUsername(username).isPresent())
                ? username
                : "Token expired";
    }
}
