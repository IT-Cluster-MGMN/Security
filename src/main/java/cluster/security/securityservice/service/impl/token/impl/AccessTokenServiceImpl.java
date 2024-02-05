package cluster.security.securityservice.service.impl.token.impl;


import cluster.security.securityservice.config.keys.AccessRsaKeyConfig;
import cluster.security.securityservice.config.keys.RefreshRsaKeyConfig;
import cluster.security.securityservice.dao.AuthorityJpaRepo;
import cluster.security.securityservice.service.UserService;
import cluster.security.securityservice.service.impl.token.AccessTokenService;
import cluster.security.securityservice.service.impl.token.JwtHelper;
import cluster.security.securityservice.util.TokenType;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenServiceImpl extends JwtHelper implements AccessTokenService {

    private final UserService userServiceImpl;
    private final AuthorityJpaRepo authorityJpaRepo;

    public AccessTokenServiceImpl(AccessRsaKeyConfig accessRsaKeyConfig,
                                  RefreshRsaKeyConfig refreshRsaKeyConfig,
                                  UserService userServiceImpl,
                                  AuthorityJpaRepo authorityJpaRepo) {
        super(accessRsaKeyConfig, refreshRsaKeyConfig, userServiceImpl);
        this.userServiceImpl = userServiceImpl;
        this.authorityJpaRepo = authorityJpaRepo;
    }

    @Override
    public String generateAccessTokenFromRefresh(String refreshToken) {
        if (isTokenExpired(refreshToken, TokenType.REFRESH))
            return null;

        final String username = getAllClaimsFromToken(refreshToken, TokenType.REFRESH).getSubject();

        if (authorityJpaRepo.findById(username).isEmpty())
            return null;

        final String roleFromClaims = getAllClaimsFromToken(refreshToken, TokenType.REFRESH).get("authority", String.class);
        final String roleFromDB = authorityJpaRepo.findById(username).get().getAuthority();

        if (!roleFromClaims.equals(roleFromDB))
            return null;

        final UserDetails userDetails = userServiceImpl.loadUserByUsername(username);

        return generateToken(userDetails);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return isTokenExpired(token, TokenType.ACCESS);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return super.generateToken(userDetails, TokenType.ACCESS);
    }

    @Override
    public String getUsernameFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token, TokenType.ACCESS);
        final String username = claims.getSubject();

        return (!isTokenExpired(token) && userServiceImpl.findByUsername(username).isPresent())
                ? username
                : "Token expired";
    }
}
