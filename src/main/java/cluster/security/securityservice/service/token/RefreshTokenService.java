package cluster.security.securityservice.service.token;


import cluster.security.securityservice.config.keys.AccessRsaKeyConfig;
import cluster.security.securityservice.config.keys.RefreshRsaKeyConfig;
import cluster.security.securityservice.util.KeyType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService extends JwtHelper implements JwtGeneration {

    public RefreshTokenService(AccessRsaKeyConfig accessRsaKeyConfig,
                               RefreshRsaKeyConfig refreshRsaKeyConfig) {
        super(accessRsaKeyConfig, refreshRsaKeyConfig);
    }


    @Override
    public String generateToken(UserDetails userDetails) {
        return super.generateToken(userDetails, KeyType.REFRESH);
    }
}
