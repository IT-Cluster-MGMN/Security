package cluster.security.securityservice.service.impl;


import cluster.security.securityservice.model.dtos.*;
import cluster.security.securityservice.service.JwtService;
import cluster.security.securityservice.service.UserService;
import cluster.security.securityservice.service.impl.token.AccessTokenService;
import cluster.security.securityservice.config.keys.AccessRsaKeyConfig;
import cluster.security.securityservice.service.impl.token.RefreshTokenService;
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
public class JwtServiceImpl implements JwtService {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenServiceImpl;
    private final AccessTokenService accessTokenServiceImpl;
    private final UserService userServiceImpl;
    private final AccessRsaKeyConfig keyUtils;


    @Override
    public ResponseEntity<?> getAccessTokenAndSetAllTokens(JwtRequest authRequest, HttpServletResponse response) {
        try {
            authenticateUserFromRequest(authRequest);
        } catch (BadCredentialsException e) {
            return authExceptionBody();
        }

        configureAllCookies(authRequest, response);

        return ResponseEntity.ok(
                new JwtResponse(accessToken(authRequest)));
    }

    @Override
    public ResponseEntity<?> registerAndLogin(UserRegistration userRegistration,
                                              HttpServletResponse response) {
        userServiceImpl.save(userRegistration);
        final JwtRequest jwtRequest = parseUserRegistrationIntoJwtRequest(userRegistration);
        final Tokens tokens = new Tokens();

        tokens.setRefreshToken(refreshToken(jwtRequest));
        tokens.setAccessToken(accessToken(jwtRequest));

        return ResponseEntity.ok(tokens);
    }

    @Override
    public String updatedAccessToken(String refreshToken) {
        if (refreshTokenServiceImpl.isTokenExpired(refreshToken))
            return null;

        return accessTokenServiceImpl.generateAccessTokenFromRefresh(refreshToken);
    }

    @Override
    public ResponseEntity<?> getPublicKey() {
        return ResponseEntity.ok(Base64.getEncoder().encodeToString(keyUtils.publicKey().getEncoded()));
    }

    @Override
    public ResponseEntity<?> getUsernameFromToken(HttpServletRequest request) {
        String token = getTokenFromCookie(request, TokenType.ACCESS);

        if (token == null || token.isEmpty())
            return ResponseEntity.badRequest().body("Access token not found in cookies");

        String username = accessTokenServiceImpl.getUsernameFromToken(token);

        return ResponseEntity.ok(new UsernameResponse(username));
    }

    @Override
    public ResponseEntity<?> removeTokensFromCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null)
            return cookieExceptionBody();

        for (Cookie cookie : cookies) {
            if ("accessToken".equals(cookie.getName()) || "refreshToken".equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                response.addCookie(cookie);
            }
        }

        return ResponseEntity.ok("Tokens removed successfully");
    }

    @Override
    public IsLoggedInResponse isLoggedIn(HttpServletRequest request) {
        String accessToken = getTokenFromCookie(request, TokenType.ACCESS);
        String refreshToken = getTokenFromCookie(request, TokenType.REFRESH);

        final IsLoggedInResponse loggedIn = new IsLoggedInResponse(true);
        final IsLoggedInResponse notLoggedIn = new IsLoggedInResponse(false);

        if ((refreshToken == null) && (accessToken == null))
            return notLoggedIn;

        if ((accessToken != null) && (refreshToken != null)) {
            if (accessTokenServiceImpl.isTokenExpired(accessToken)
                    && refreshTokenServiceImpl.isTokenExpired(refreshToken)) {
                return notLoggedIn;
            }
        }

        if (accessToken != null) {
            if (accessTokenServiceImpl.isTokenExpired(accessToken))
                return notLoggedIn;
        }

        if (refreshToken != null) {
            if (refreshTokenServiceImpl.isTokenExpired(refreshToken))
                return notLoggedIn;
        }

        return loggedIn;
    }

    private void configureAllCookies(JwtRequest authRequest, HttpServletResponse response) {
        response.addHeader("Set-Cookie", configuredCookie(authRequest, TokenType.ACCESS).toString());
        response.addHeader("Set-Cookie", configuredCookie(authRequest, TokenType.REFRESH).toString());
    }

    private JwtRequest parseUserRegistrationIntoJwtRequest(UserRegistration userRegistration) {
        return new JwtRequest(
                userRegistration.getUser().getUsername(),
                userRegistration.getUser().getPassword()
        );
    }

    private void authenticateUserFromRequest(JwtRequest authRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(),
                authRequest.getPassword()));
    }

    private String getTokenFromCookie(HttpServletRequest request, TokenType type) {
        Cookie[] cookies = request.getCookies();
        final String tokenType = (type == TokenType.ACCESS)
                ? "accessToken"
                : "refreshToken";

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenType.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
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
        return accessTokenServiceImpl.generateToken(getUserDetailsFromJwtRequest(authRequest));
    }

    private String refreshToken(JwtRequest authRequest) {
        return refreshTokenServiceImpl.generateToken(getUserDetailsFromJwtRequest(authRequest));
    }

    private UserDetails getUserDetailsFromJwtRequest(JwtRequest authRequest) {
        return userServiceImpl.loadUserByUsername(authRequest.getUsername());
    }

    private ResponseEntity<?> authExceptionBody() {
        return new ResponseEntity<>(new AuthError(
                HttpStatus.UNAUTHORIZED.value(),
                "Wrong username or password",
                System.currentTimeMillis()),
                HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<?> cookieExceptionBody() {
        return new ResponseEntity<>("Cookie is null",
                HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> userAlreadyExistsExceptionBody() {
        return new ResponseEntity<>(new AuthError(
           HttpStatus.BAD_REQUEST.value(),
           "User with such username already exists",
           System.currentTimeMillis()
        ), HttpStatus.BAD_REQUEST);
    }
}
