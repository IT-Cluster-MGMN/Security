package cluster.security.securityservice.model.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "authorities")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Authority {

    @Id
    private String username;
    private String authority;
}
