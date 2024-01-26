package cluster.security.securityservice.model.dtos;


import cluster.security.securityservice.model.entity.Authority;
import cluster.security.securityservice.model.entity.User;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
public class UserRegistration {

    private User user;
    private final Authority authority;


    public UserRegistration(@Pattern(regexp="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
                            @Size(min = 3, max = 50) String username,
                            String password) {
        this.user = User.builder()
                .username(username)
                .password(password)
                .enabled(1)
                .build();

        this.authority = Authority.builder()
                .username(username)
                .authority("ROLE_USER")
                .build();
    }

    public void setPassword(String password) {
        user.setPassword(password);
    }
}
