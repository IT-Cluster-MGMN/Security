package cluster.security.securityservice.model;


import cluster.security.securityservice.model.entity.Authority;
import cluster.security.securityservice.model.entity.User;
import cluster.security.securityservice.model.entity.UserInfo;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
public class UserRegistration {

    private User user;
    private final Authority authority;
    private final UserInfo userInfo;


    public UserRegistration(@Size(min = 1, max = 20) String name,
                            @Size(min = 1, max = 20) String surname,
                            @Size(min = 1, max = 20) String patronymic,
                            @Size(min = 10, max = 12) String number,
                            @Pattern(regexp="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
                            @Size(min = 3, max = 50) String username,
                            @Size(min = 8, max = 80) String password) {
        this.user = User.builder()
                .username(username)
                .password(password)
                .enabled(1)
                .build();

        this.authority = Authority.builder()
                .username(username)
                .authority("ROLE_USER")
                .build();

        this.userInfo = UserInfo.builder()
                .username(username)
                .name(name)
                .surname(surname)
                .patronymic(patronymic)
                .number(number)
                .build();
    }

    public void setPassword(String password) {
        user.setPassword(password);
    }
}
