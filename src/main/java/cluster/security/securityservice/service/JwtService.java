package cluster.security.securityservice.service;


import cluster.security.securityservice.model.dtos.AuthError;
import cluster.security.securityservice.model.dtos.JwtRequest;
import cluster.security.securityservice.model.dtos.JwtResponse;
import cluster.security.securityservice.model.dtos.UsernameResponse;
import cluster.security.securityservice.service.token.AccessTokenService;
import cluster.security.securityservice.service.token.JwtGeneration;
import cluster.security.securityservice.config.keys.AccessRsaKeyConfig;
import cluster.security.securityservice.util.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

    private final UserService userService;
    private final AccessRsaKeyConfig keyUtils;
    private final AccessTokenService tokenService;
    private final JwtGeneration accessTokenService;
    private final JwtGeneration refreshTokenService;
    private final AuthenticationManager authenticationManager;


    public ResponseEntity<?> getAccessTokenAndSetAllTokens(JwtRequest authRequest, HttpServletResponse response) {
        try {
            authenticateUserFromRequest(authRequest);
        } catch (BadCredentialsException e) {
            return authExceptionBody();
        }

        response.addHeader("Set-Cookie", configuredCookie(authRequest, TokenType.ACCESS).toString());
        response.addHeader("Set-Cookie", configuredCookie(authRequest, TokenType.REFRESH).toString());

        return ResponseEntity.ok(
                new JwtResponse(accessToken(authRequest)));
    }

    public String updatedAccessToken(String refreshToken) {
        if (refreshToken == null)
            return null;

        if (tokenService.isTokenExpired(refreshToken, TokenType.REFRESH))
            return null;

        return tokenService.generateAccessFromRefresh(refreshToken);
    }

    public ResponseEntity<?> getPublicKey() {
        return ResponseEntity.ok(Base64.getEncoder().encodeToString(keyUtils.publicKey().getEncoded()));
    }

    public ResponseEntity<?> getUsernameFromToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }

        }
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("Access token not found in cookies");
        }
        String username = tokenService.getUsernameFromToken(token);

        return ResponseEntity.ok(new UsernameResponse(username));
    }


    private void authenticateUserFromRequest(JwtRequest authRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(),
                authRequest.getPassword()));
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

        return ResponseCookie.from(cookieName, cookieValue)
                .httpOnly(httpOnly)
                .sameSite("None")
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .build();
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
}
