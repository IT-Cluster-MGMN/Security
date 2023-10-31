package cluster.security.securityservice.controller;


import cluster.security.securityservice.model.entity.User;
import cluster.security.securityservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    @ResponseBody
    public Iterable<User> findAllUsers() {
        return userService.findAll();
    }
}
