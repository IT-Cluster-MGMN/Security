package cluster.security.securityservice.model.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private String username;
    private String token;
}
