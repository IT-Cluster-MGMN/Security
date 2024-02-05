package cluster.security.securityservice.service.impl.token.impl;


import cluster.security.securityservice.config.keys.AccessRsaKeyConfig;
import cluster.security.securityservice.config.keys.RefreshRsaKeyConfig;
import cluster.security.securityservice.service.UserService;
import cluster.security.securityservice.service.impl.token.JwtHelper;
import cluster.security.securityservice.service.impl.token.RefreshTokenService;
import cluster.security.securityservice.util.TokenType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl extends JwtHelper implements RefreshTokenService {

    public RefreshTokenServiceImpl(AccessRsaKeyConfig accessRsaKeyConfig,
                                   RefreshRsaKeyConfig refreshRsaKeyConfig,
                                   UserService userServiceImpl) {
        super(accessRsaKeyConfig, refreshRsaKeyConfig, userServiceImpl);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return super.generateToken(userDetails, TokenType.REFRESH);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return isTokenExpired(token, TokenType.REFRESH);
    }
}
