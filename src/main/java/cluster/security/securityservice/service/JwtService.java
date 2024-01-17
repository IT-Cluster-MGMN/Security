package cluster.security.securityservice.service;


import cluster.security.securityservice.model.dtos.AuthError;
import cluster.security.securityservice.model.dtos.JwtRequest;
import cluster.security.securityservice.model.dtos.JwtResponse;
import cluster.security.securityservice.service.token.AccessTokenService;
import cluster.security.securityservice.service.token.JwtGeneration;
import cluster.security.securityservice.config.keys.AccessRsaKeyConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
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

    private final AuthenticationManager authenticationManager;
    private final AccessTokenService updateAccessToken;
    private final JwtGeneration refreshTokenService;
    private final JwtGeneration accessTokenService;
    private final AccessRsaKeyConfig keyUtils;
    private final UserService userService;


    public ResponseEntity<?> getAllTokens(JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return authExceptionBody();
        }

        return ResponseEntity.ok(
                new JwtResponse(accessToken(authRequest),
                                refreshToken(authRequest)));
    }

    public ResponseEntity<?> updateAccessToken(String refreshToken) {
        return (updateAccessToken.isTokenExpired(refreshToken))
                ? refreshTokenExceptionBody()
                : ResponseEntity.ok(new AccessTokenResponse(updateAccessToken.generateAccessFromRefresh(refreshToken)));
    }

    public ResponseEntity<?> getPublicKey() {
        return ResponseEntity.ok(Base64.getEncoder().encodeToString(keyUtils.publicKey().getEncoded()));
    }

    private String accessToken(JwtRequest authRequest) {
        return accessTokenService.generateToken(getUserDetailsFromJwtRequest(authRequest));
    }

    private String refreshToken(JwtRequest authRequest) {
        return refreshTokenService.generateToken(getUserDetailsFromJwtRequest(authRequest));
    }

    private UserDetails getUserDetailsFromJwtRequest(JwtRequest authRequest) {
        return userService.loadUserByUsername(authRequest.getUsername());
    }

    private ResponseEntity<?> authExceptionBody() {
        return new ResponseEntity<>(new AuthError(
                HttpStatus.UNAUTHORIZED.value(),
                "Wrong username or password",
                System.currentTimeMillis()),
                HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<?> refreshTokenExceptionBody() {
        return new ResponseEntity<>(new AuthError(
                HttpStatus.BAD_REQUEST.value(),
                "Refresh token is expired or invalid",
                System.currentTimeMillis()),
                HttpStatus.BAD_REQUEST);
    }


    private record AccessTokenResponse(String accessToken) {}
}
