package cluster.security.securityservice.controller;

import cluster.security.securityservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


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
    public ResponseEntity<?> updateAccessToken(@RequestBody Map<String, String> requestBody) {
        final String refreshToken = requestBody.get("refreshToken");
        return ResponseEntity.ok(jwtService.updatedAccessToken(refreshToken));
    }



}
