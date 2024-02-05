package cluster.security.securityservice.controller;


import cluster.security.securityservice.model.entity.User;
import cluster.security.securityservice.service.JwtService;
import cluster.security.securityservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/data")
public class UserController {

    private final UserService userServiceImpl;
    private final JwtService jwtServiceImpl;


    // temp {
    @GetMapping("/info")
    @ResponseBody
    public Iterable<User> findAllUsers() {
        return userServiceImpl.findAll();
    }
    // }


    @GetMapping("/username")
    public ResponseEntity<?> getUsernameFromToken(HttpServletRequest request) {
        return jwtServiceImpl.getUsernameFromToken(request);
    }
}
