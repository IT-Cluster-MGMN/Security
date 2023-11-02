package cluster.security.securityservice.controller;


import cluster.security.securityservice.model.dtos.UserRegistration;
import cluster.security.securityservice.model.dtos.JwtRequest;
import cluster.security.securityservice.service.JwtService;
import cluster.security.securityservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        return jwtService.createAuthToken(authRequest);
    }

    @PostMapping("/register")
    public void createNewUser(@RequestBody UserRegistration userRegistration) {
        userService.save(userRegistration);
    }
}
