package cluster.security.securityservice.service.token;


import cluster.security.securityservice.dao.AuthorityJpaRepo;
import cluster.security.securityservice.model.dtos.JwtResponse;
import cluster.security.securityservice.service.UserService;
import cluster.security.securityservice.util.KeyType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final UserService userService;
    private final JwtGeneration tokenUtils;
    private final AuthorityJpaRepo authorityJpaRepo;

    public JwtResponse generateAccessFromRefresh(String refreshToken) {
        if (!tokenUtils.isTokenExpired(refreshToken)) {
            final String username = tokenUtils.getAllClaimsFromToken(refreshToken).getSubject();
            final String role = tokenUtils.getAllClaimsFromToken(refreshToken)
                    .get("authority", String.class);

            if (authorityJpaRepo.findById(username).isPresent()) {
                if (!role.equals(authorityJpaRepo.findById(username).get().getAuthority())) {
                    return null;
                }
            } else {
                return null;
            }

            final UserDetails userDetails = userService.loadUserByUsername(username);
            final String accessToken = tokenUtils.generateToken(userDetails, KeyType.ACCESS);
            System.out.println(accessToken);

            return new JwtResponse(accessToken);
        }
        return null;
    }

}
