package cluster.security.securityservice.service;


import cluster.security.securityservice.model.dtos.AuthError;
import cluster.security.securityservice.model.dtos.JwtRequest;
import cluster.security.securityservice.model.dtos.JwtResponse;
import cluster.security.securityservice.model.entity.RefreshToken;
import cluster.security.securityservice.service.token.AccessTokenService;
import cluster.security.securityservice.service.token.RefreshTokenService;
import cluster.security.securityservice.service.token.JwtGeneration;
import cluster.security.securityservice.config.keys.AccessRsaKeyConfig;
import cluster.security.securityservice.util.KeyType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Base64;


@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserService userService;
    private final JwtGeneration jwtGeneration;
    private final AccessRsaKeyConfig keyUtils;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;


    public ResponseEntity<?> getPublicKey() {
        return ResponseEntity.ok(Base64.getEncoder().encodeToString(keyUtils.publicKey().getEncoded()));
    }

    public ResponseEntity<?> getToken(JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AuthError(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Wrong username or password",
                    System.currentTimeMillis()),
                    HttpStatus.UNAUTHORIZED);
        }
//
//        if (jwtGeneration.isTokenExpired(refreshTokenService.findById(authRequest.getUsername()).getToken())) {
//            final UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
//            final String token = jwtGeneration.generateToken(userDetails, KeyType.REFRESH);
//            final RefreshToken refreshToken = RefreshToken.builder()
//                    .username(userDetails.getUsername())
//                    .token(token)
//                    .build();
//            updateOrCreateRefreshToken(refreshToken);
//
//            return ResponseEntity.ok(accessTokenService.generateAccessFromRefresh(token));
//        }
//
//        return accessToken(authRequest.getUsername());
        return ResponseEntity.ok(new JwtResponse(jwtGeneration.generateToken(
                userService.loadUserByUsername(authRequest.getUsername()), KeyType.ACCESS)));
    }

    public ResponseEntity<?> accessToken(String username) {
        final String token = refreshTokenService.findById(username).getToken();

        return ResponseEntity.ok(accessTokenService.generateAccessFromRefresh(token));
    }

    private void updateOrCreateRefreshToken(RefreshToken refreshToken) {
        boolean isUpdated = refreshTokenService.update(refreshToken);
        if (!isUpdated) {
            refreshTokenService.save(refreshToken);
        }
    }

}
