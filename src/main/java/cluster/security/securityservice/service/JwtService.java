package cluster.security.securityservice.service;

import cluster.security.securityservice.model.dtos.IsLoggedInResponse;
import cluster.security.securityservice.model.dtos.JwtRequest;
import cluster.security.securityservice.model.dtos.UserRegistration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface JwtService {

    ResponseEntity<?> getAccessTokenAndSetAllTokens(JwtRequest authRequest, HttpServletResponse response);

    void save(UserRegistration userRegistration, HttpServletResponse response);

    String updatedAccessToken(String refreshToken);

    ResponseEntity<?> getPublicKey();

    ResponseEntity<?> getUsernameFromToken(HttpServletRequest request);

    ResponseEntity<?> removeTokensFromCookie(HttpServletRequest request, HttpServletResponse response);

    IsLoggedInResponse isLoggedIn(HttpServletRequest request);
}
