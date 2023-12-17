package cluster.security.securityservice.service.token;


import cluster.security.securityservice.config.AccessRsaKeyConfig;
import cluster.security.securityservice.config.RefreshRsaKeyConfig;
import cluster.security.securityservice.util.KeyType;
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
public class JwtGeneration {

    private final AccessRsaKeyConfig accessRsaKeyConfig;
    private final RefreshRsaKeyConfig refreshRsaKeyConfig;


    public String generateToken(UserDetails userDetails, KeyType keyType) {
        Map<String, Object> claims = new HashMap<>();
        List<String> rolesList = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("authority", rolesList.get(0));

        Date issuedDate = new Date();
//        Duration lifetime = (keyType == KeyType.ACCESS) ? Duration.ofHours(1) : Duration.ofDays(7);
        Duration lifetime = (keyType == KeyType.ACCESS) ? Duration.ofSeconds(15) : Duration.ofDays(7);
        Date expiredDate = new Date(issuedDate.getTime() + lifetime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith((keyType == KeyType.ACCESS) ?
                        accessRsaKeyConfig.privateKey() : refreshRsaKeyConfig.privateKey())
                .compact();
    }

    public Claims getAllClaimsFromToken(String token) {
        final PublicKey key = refreshRsaKeyConfig.publicKey();
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean isTokenExpired(String token) {
        try {
            return this.getAllClaimsFromToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

}
