package cluster.security.securityservice.controller;


import cluster.security.securityservice.model.dtos.IsLoggedInResponse;
import cluster.security.securityservice.model.dtos.UserRegistration;
import cluster.security.securityservice.model.dtos.JwtRequest;
import cluster.security.securityservice.service.JwtService;
import cluster.security.securityservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/security")
public class AuthController {

    private final JwtService jwtServiceImpl;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest,
                                             HttpServletResponse response) {
        return jwtServiceImpl.getAccessTokenAndSetAllTokens(authRequest, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response) {
        return jwtServiceImpl.removeTokensFromCookie(request, response);
    }

    @PostMapping("/register")
    public void createUser(@RequestBody UserRegistration userRegistration,
                           HttpServletResponse response) {
        jwtServiceImpl.registerAndLogin(userRegistration, response);
    }

    @GetMapping("/is-logged-in")
    public IsLoggedInResponse isLoggedIn(HttpServletRequest request) {
        return jwtServiceImpl.isLoggedIn(request);
    }
}
