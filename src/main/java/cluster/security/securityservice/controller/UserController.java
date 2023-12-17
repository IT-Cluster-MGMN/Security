package cluster.security.securityservice.controller;


import cluster.security.securityservice.model.entity.User;
import cluster.security.securityservice.service.UserService;
import cluster.security.securityservice.service.token.JwtGeneration;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/data")
public class UserController {

    private final UserService userService;
    private final JwtGeneration jwtGeneration;

    @GetMapping("/info")
    @ResponseBody
    public Iterable<User> findAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/username")
    @ResponseBody
    public String username(String token) {
        return jwtGeneration.getAllClaimsFromToken(token).getSubject();
    }
}
