package cluster.security.securityservice.service.impl.token;


import cluster.security.securityservice.config.keys.AccessRsaKeyConfig;
import cluster.security.securityservice.config.keys.RefreshRsaKeyConfig;
import cluster.security.securityservice.dao.UserJpaRepo;
import cluster.security.securityservice.model.entity.User;
import cluster.security.securityservice.service.UserService;
import cluster.security.securityservice.util.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.time.Duration;
import java.util.*;


@Component
@RequiredArgsConstructor
public abstract class JwtHelper {

    private final AccessRsaKeyConfig accessRsaKeyConfig;
    private final RefreshRsaKeyConfig refreshRsaKeyConfig;
    private final UserService userServiceImpl;


    protected String generateToken(UserDetails userDetails, TokenType tokenType) {
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("authority", rolesList.get(0));

        Date issuedDate = new Date();

        Duration lifetime = (tokenType == TokenType.ACCESS) ? Duration.ofHours(6) : Duration.ofDays(7);
        Date expiredDate = new Date(issuedDate.getTime() + lifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith((tokenType == TokenType.ACCESS)
                        ? accessRsaKeyConfig.privateKey()
                        : refreshRsaKeyConfig.privateKey())
                .compact();
    }

    protected Claims getAllClaimsFromToken(String token, TokenType tokenType) {
        final PublicKey key = (tokenType == TokenType.ACCESS)
                ? accessRsaKeyConfig.publicKey()
                : refreshRsaKeyConfig.publicKey();
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean isTokenExpired(String token, TokenType tokenType) {
        if (token == null)
            return true;
        String username = getAllClaimsFromToken(token, tokenType).getSubject();
        if (userServiceImpl.findByUsername(username).isEmpty())
            return true;
        try {
            return (tokenType == TokenType.ACCESS)
                    ? this.getAllClaimsFromToken(token, TokenType.ACCESS).getExpiration().before(new Date())
                    : this.getAllClaimsFromToken(token, TokenType.REFRESH).getExpiration().before(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

}
