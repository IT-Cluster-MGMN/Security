package cluster.security.securityservice.model.dtos;

import lombok.Data;

@Data
public class JwtRequest {

    private String username;
    private String password;
}
