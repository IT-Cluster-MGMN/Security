package cluster.security.securityservice.controller;


import cluster.security.securityservice.model.entity.User;
import cluster.security.securityservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/data")
public class UserControllerTest {

    private final UserService userService;

    @GetMapping("/info")
    @ResponseBody
    public Iterable<User> findAllUsers() {
        return userService.findAll();
    }

}
