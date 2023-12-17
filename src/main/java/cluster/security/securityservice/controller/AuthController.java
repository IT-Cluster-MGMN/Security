package cluster.security.securityservice.controller;


import cluster.security.securityservice.model.dtos.UserRegistration;
import cluster.security.securityservice.model.dtos.JwtRequest;
import cluster.security.securityservice.service.JwtService;
import cluster.security.securityservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/security")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return jwtService.getToken(authRequest);
    }

    @PostMapping("/register")
    public void createNewUser(@RequestBody UserRegistration userRegistration) {
        userService.save(userRegistration);
    }

    @PostMapping("/update-token")
    public ResponseEntity<?> updateAccessToken(@RequestBody String username) {
        return jwtService.accessToken(username);
    }
}
