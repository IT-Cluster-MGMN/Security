package cluster.security.securityservice.service;


import cluster.security.securityservice.model.dtos.AuthError;
import cluster.security.securityservice.model.dtos.JwtRequest;
import cluster.security.securityservice.model.dtos.JwtResponse;
import cluster.security.securityservice.service.token.AccessTokenService;
import cluster.security.securityservice.service.token.JwtGeneration;
import cluster.security.securityservice.config.keys.AccessRsaKeyConfig;
import cluster.security.securityservice.service.token.RefreshTokenService;
import cluster.security.securityservice.util.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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


    public ResponseEntity<?> getAccessTokenAndSetAllTokens(JwtRequest authRequest,
                                                           HttpServletResponse response) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return authExceptionBody();
        }
        final String accessToken = accessToken(authRequest);

        response.addHeader("Set-Cookie", configuredCookie(authRequest, TokenType.ACCESS).toString());
        response.addHeader("Set-Cookie", configuredCookie(authRequest, TokenType.REFRESH).toString());

        return ResponseEntity.ok(
                new JwtResponse(accessToken));
    }

    public String updateAccessToken(String refreshToken) {
        if (refreshToken == null) {
            return null;
        }
        if (updateAccessToken.isTokenExpired(refreshToken)) {
            return null;
        }

        return updateAccessToken.generateAccessFromRefresh(refreshToken);
    }

    public ResponseEntity<?> getPublicKey() {
        return ResponseEntity.ok(Base64.getEncoder().encodeToString(keyUtils.publicKey().getEncoded()));
    }

    private ResponseCookie configuredCookie(JwtRequest authRequest, TokenType tokenType) {
        String cookieName = "accessToken";
        String cookieValue = accessToken(authRequest);
        boolean httpOnly = false;
        int maxAge = 22_000;

        if (tokenType == TokenType.REFRESH) {
            cookieName = "refreshToken";
            cookieValue = refreshToken(authRequest);
            httpOnly = true;
            maxAge = 605_000;
        }
        ResponseCookie responseCookie = ResponseCookie.from(cookieName, cookieValue)
                .httpOnly(httpOnly)
                .sameSite("None")
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .build();

        return responseCookie;
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
