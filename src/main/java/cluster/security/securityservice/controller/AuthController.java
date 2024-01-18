package cluster.security.securityservice.controller;


import cluster.security.securityservice.model.dtos.UserRegistration;
import cluster.security.securityservice.model.dtos.JwtRequest;
import cluster.security.securityservice.model.entity.User;
import cluster.security.securityservice.service.JwtService;
import cluster.security.securityservice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/security")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest,
                                             HttpServletResponse response) {
        return jwtService.getAccessTokenAndSetAllTokens(authRequest, response);
    }

    @PostMapping("/register")
    public void createUser(@RequestBody UserRegistration userRegistration) {
        userService.save(userRegistration);
    }
}
