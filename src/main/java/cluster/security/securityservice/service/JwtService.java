package cluster.security.securityservice.service;


import cluster.security.securityservice.model.dtos.AuthError;
import cluster.security.securityservice.model.dtos.JwtRequest;
import cluster.security.securityservice.model.dtos.JwtResponse;
import cluster.security.securityservice.util.JwtTokenUtils;
import cluster.security.securityservice.util.RsaKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;


@Service
@RequiredArgsConstructor
public class JwtService {

    private final RsaKeyUtils keyUtils;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;


    public ResponseEntity<?> createAuthToken(JwtRequest authRequest) {
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
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> getPublicKey() {
        return ResponseEntity.ok(Base64.getEncoder().encodeToString(keyUtils.publicKey().getEncoded()));
    }
}
