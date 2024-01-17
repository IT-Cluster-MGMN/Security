package cluster.security.securityservice.controller;

import cluster.security.securityservice.service.JwtService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/jwt")
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;

    @GetMapping("/public-key")
    public ResponseEntity<?> getPublicKey() {
        return jwtService.getPublicKey();
    }

    @PostMapping("/update-token")
    public ResponseEntity<?> updateAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return jwtService.updateAccessToken(refreshTokenRequest.refreshToken());
    }


    private record RefreshTokenRequest(String refreshToken) {}
}
