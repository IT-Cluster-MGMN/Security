package cluster.security.securityservice.model.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserInfo {

    @Id
    private String username;
    private String name;
    private String surname;
    private String number;
}
