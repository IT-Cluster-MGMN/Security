package cluster.security.securityservice.utils;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class JwtTokenUtilsTest {

    private final JwtTokenUtils jwtTokenUtils = new JwtTokenUtils();


    @Test
    public void generateTokenTest() {
        UserDetails userDetails = User.builder()
                .username("username")
                .password("{bcrypt}$2a$10$wtiimmlU3CHrmt25ehNX7OGmyOpe.vfCe7MA4PZ9jpRvLq7Dts6Z2")
                .authorities("ROLE_USER")
                .build();

        System.out.println(jwtTokenUtils.generateToken(userDetails));
    }
}