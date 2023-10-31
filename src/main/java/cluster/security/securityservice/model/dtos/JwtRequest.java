package cluster.security.securityservice.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class JwtRequest {

    private String username;
    private String password;
}
